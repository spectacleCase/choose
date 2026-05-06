package com.choose.service.agent.cache;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 两级缓存 (论文 5.1):
 *   L1 = Caffeine 本地缓存 (节点内,纳秒级访问)
 *   L2 = Redis 分布式缓存 (跨节点共享)
 *
 * 命中顺序: L1 → L2 → 回源加载。回源后回填 L1+L2。
 * "先更新DB再删除缓存"模式: 通过 invalidate(ns, key) 实现。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TwoLevelCache {

    private final StringRedisTemplate redis;

    /** namespace 到 Caffeine 实例的映射 */
    private final Map<String, Cache<String, String>> caffeineMap = new ConcurrentHashMap<>();

    /** L2 命中计数 (Caffeine 内置 L1 stats) */
    private final Map<String, AtomicLong> l2HitMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> l2MissMap = new ConcurrentHashMap<>();

    /**
     * 从两级缓存读取,缺失则调 loader,自动回填两级。
     * 值统一以 JSON 字符串形式存储,避免序列化兼容问题。
     */
    public <T> T get(String namespace, String key, Class<T> clazz, Function<String, T> loader) {
        Cache<String, String> caf = caffeineFor(namespace);
        String cached = caf.getIfPresent(key);
        if (cached != null) {
            return JSON.parseObject(cached, clazz);
        }

        // L2
        String redisKey = redisKey(namespace, key);
        try {
            String json = redis.opsForValue().get(redisKey);
            if (json != null) {
                l2Hit(namespace);
                caf.put(key, json);
                return JSON.parseObject(json, clazz);
            }
        } catch (Exception e) {
            log.debug("redis get fail ns={} key={}", namespace, key);
        }
        l2Miss(namespace);

        // 回源
        T value = loader.apply(key);
        if (value != null) {
            String json = JSON.toJSONString(value);
            caf.put(key, json);
            try {
                redis.opsForValue().set(redisKey, json, ttlMinutes(namespace), TimeUnit.MINUTES);
            } catch (Exception e) {
                log.debug("redis set fail ns={} key={}", namespace, key);
            }
        }
        return value;
    }

    /** "先更新DB再删除缓存"用 (论文 5.1)。两级一起删。 */
    public void invalidate(String namespace, String key) {
        Cache<String, String> caf = caffeineMap.get(namespace);
        if (caf != null) caf.invalidate(key);
        try {
            redis.delete(redisKey(namespace, key));
        } catch (Exception ignored) {}
    }

    /** 全 namespace 清空 (谨慎用) */
    public void invalidateNamespace(String namespace) {
        Cache<String, String> caf = caffeineMap.get(namespace);
        if (caf != null) caf.invalidateAll();
    }

    /** 命中率统计,供管理端 GET /agent/cache/stats 用 */
    public Map<String, Map<String, Object>> stats() {
        Map<String, Map<String, Object>> out = new LinkedHashMap<>();
        for (Map.Entry<String, Cache<String, String>> e : caffeineMap.entrySet()) {
            String ns = e.getKey();
            CacheStats s = e.getValue().stats();
            long l1Hit = s.hitCount();
            long l1Miss = s.missCount();
            long l2Hit = l2HitMap.getOrDefault(ns, new AtomicLong()).get();
            long l2Miss = l2MissMap.getOrDefault(ns, new AtomicLong()).get();
            long total = l1Hit + l1Miss;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("namespace", ns);
            m.put("l1Size", e.getValue().estimatedSize());
            m.put("l1Hit", l1Hit);
            m.put("l1Miss", l1Miss);
            m.put("l1HitRate", total == 0 ? 0.0 : Math.round((double) l1Hit / total * 10000) / 10000.0);
            m.put("l2Hit", l2Hit);
            m.put("l2Miss", l2Miss);
            m.put("evictions", s.evictionCount());
            out.put(ns, m);
        }
        return out;
    }

    // ---------- 内部 ----------

    private Cache<String, String> caffeineFor(String ns) {
        return caffeineMap.computeIfAbsent(ns, this::createCache);
    }

    private Cache<String, String> createCache(String ns) {
        // 不同 namespace 不同容量/过期 (后续可外部化配置)
        return Caffeine.newBuilder()
                .maximumSize(maxSize(ns))
                .expireAfterWrite(Duration.ofMinutes(localTtlMinutes(ns)))
                .recordStats()
                .build();
    }

    private long maxSize(String ns) {
        return switch (ns) {
            case "amap.distance"  -> 2000;  // 高德距离查询
            case "amap.geocode"   -> 1000;
            case "nutrition.llm"  -> 500;
            case "dish.query"     -> 200;
            default                -> 500;
        };
    }

    private long localTtlMinutes(String ns) {
        return switch (ns) {
            case "amap.distance" -> 30;
            case "amap.geocode"  -> 60;
            case "nutrition.llm" -> 60;
            case "dish.query"    -> 5;
            default               -> 10;
        };
    }

    private long ttlMinutes(String ns) {
        // L2 的 TTL 一般比 L1 长
        return Math.max(60, localTtlMinutes(ns) * 2);
    }

    private String redisKey(String ns, String key) {
        return "agent:cache:" + ns + ":" + key;
    }

    private void l2Hit(String ns) { l2HitMap.computeIfAbsent(ns, k -> new AtomicLong()).incrementAndGet(); }
    private void l2Miss(String ns) { l2MissMap.computeIfAbsent(ns, k -> new AtomicLong()).incrementAndGet(); }
}
