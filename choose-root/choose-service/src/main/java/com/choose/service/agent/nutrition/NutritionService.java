package com.choose.service.agent.nutrition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.DishNutrition;
import com.choose.mapper.DishNutritionMapper;
import com.choose.service.agent.core.AgentLLMClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 营养数据服务。先查DB,缺失则调LLM估算并写入缓存。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NutritionService extends ServiceImpl<DishNutritionMapper, DishNutrition> {

    private final AgentLLMClient llmClient;
    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "agent:nutrition:";
    private static final long TTL_HOURS = 24;

    public DishNutrition getByDishId(Long dishId) {
        if (dishId == null) return null;
        return getOne(new LambdaQueryWrapper<DishNutrition>().eq(DishNutrition::getDishId, dishId));
    }

    public List<DishNutrition> listByDishIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return list(new LambdaQueryWrapper<DishNutrition>().in(DishNutrition::getDishId, ids));
    }

    /**
     * 让LLM按菜名估算营养。失败时返回保守默认值。
     */
    public DishNutrition estimateByName(Long dishId, String dishName) {
        String cacheKey = CACHE_PREFIX + dishName;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        DishNutrition n;
        if (cached != null) {
            n = parseLlmJson(cached);
            if (n != null) {
                n.setDishId(dishId);
                n.setSource("llm_estimate");
                return n;
            }
        }
        String sys = "你是营养师。基于中国餐饮常见做法,估算给定菜品一份的营养成分。" +
                "严格输出JSON: {\"calorie\":kcal,\"protein\":g,\"fat\":g,\"carbs\":g,\"fiber\":g,\"sodium\":mg,\"tags\":[\"低脂\"|\"高蛋白\"|\"高热量\"|\"清淡\" 等]}";
        try {
            String raw = llmClient.chat(sys, "菜品名: " + dishName);
            n = parseLlmJson(raw);
            if (n == null) n = fallback();
            redisTemplate.opsForValue().set(cacheKey, raw, TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("LLM estimate failed for {}", dishName, e);
            n = fallback();
        }
        n.setDishId(dishId);
        n.setSource("llm_estimate");
        return n;
    }

    private DishNutrition parseLlmJson(String raw) {
        try {
            String s = raw == null ? "" : raw.trim();
            int lb = s.indexOf('{');
            int rb = s.lastIndexOf('}');
            if (lb < 0 || rb <= lb) return null;
            com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSON.parseObject(s.substring(lb, rb + 1));
            DishNutrition n = new DishNutrition();
            n.setCalorie(obj.getDouble("calorie"));
            n.setProtein(obj.getDouble("protein"));
            n.setFat(obj.getDouble("fat"));
            n.setCarbs(obj.getDouble("carbs"));
            n.setFiber(obj.getDouble("fiber"));
            n.setSodium(obj.getDouble("sodium"));
            if (obj.containsKey("tags")) n.setHealthTags(obj.getJSONArray("tags").toJSONString());
            return n;
        } catch (Exception e) {
            return null;
        }
    }

    private DishNutrition fallback() {
        DishNutrition n = new DishNutrition();
        n.setCalorie(450.0);
        n.setProtein(15.0);
        n.setFat(18.0);
        n.setCarbs(50.0);
        n.setFiber(3.0);
        n.setSodium(800.0);
        n.setHealthTags("[]");
        return n;
    }
}
