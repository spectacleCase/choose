package com.choose.service.agent.coordinator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.agent.core.AgentContext;
import com.choose.service.agent.core.AgentResult;
import com.choose.service.agent.impl.DishRetrievalAgent;
import com.choose.service.agent.impl.GeoMatchAgent;
import com.choose.service.agent.impl.IntentParseAgent;
import com.choose.service.agent.impl.NutritionAgent;
import com.choose.service.agent.impl.ResultIntegrateAgent;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.service.recommend.Impl.CascadeHybridRecommendationStrategy;
import com.choose.service.recommend.RecommenderSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Agent协调器。论文4.2/5.1的多Agent协作流程控制器。
 * 流程: IntentParse -> [冲突时短路返回追问] -> DishRetrieval ->
 *      并行(Nutrition, GeoMatch) -> ResultIntegrate
 * 任一环节失败则降级到原有协同过滤策略。支持多轮追问闭环。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AgentCoordinator {

    private final IntentParseAgent intentParseAgent;
    private final DishRetrievalAgent dishRetrievalAgent;
    private final NutritionAgent nutritionAgent;
    private final GeoMatchAgent geoMatchAgent;
    private final ResultIntegrateAgent resultIntegrateAgent;
    private final AgentCallLogService logService;
    private final CascadeHybridRecommendationStrategy fallbackStrategy;
    private final StringRedisTemplate redisTemplate;

    private static final String SESSION_PREFIX = "agent:session:";
    private static final long SESSION_TTL_MIN = 5;
    private static final String ENRICHED_PREFIX = "agent:enriched:";
    private static final long ENRICHED_TTL_MIN = 5;

    /** 营养评估和地理匹配的并行池 */
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    /**
     * 首次推荐入口。
     * 若意图解析检测到矛盾约束,返回带追问的 ctx 并把会话存入 Redis 等待用户回复。
     */
    public AgentContext run(String userQuery, String userId, String userLocation, int topK) {
        return run(userQuery, userId, userLocation, topK, false);
    }

    /**
     * @param asyncNutrition true 时主流程不等 NutritionAgent (论文 3.2 "营养评估非核心,
     *                       异步计算后补充展示")。营养完成后会写入 Redis,前端通过
     *                       GET /agent/recommend/{traceId}/enriched 轮询拿增量结果。
     */
    public AgentContext run(String userQuery, String userId, String userLocation,
                            int topK, boolean asyncNutrition) {
        AgentContext ctx = new AgentContext();
        ctx.setUserQuery(userQuery);
        ctx.setUserId(userId);
        ctx.setUserLocation(userLocation);
        ctx.setTopK(topK);

        // 1. 意图解析
        AgentResult ir = intentParseAgent.execute(ctx, userQuery);
        logService.recordAsync(ctx.getTraceId(), userId, userQuery, ir);
        if (!ir.isSuccess()) {
            log.warn("[trace={}] intent parse failed, fallback", ctx.getTraceId());
            applyFallback(ctx);
            return ctx;
        }
        JSONObject intent = toJsonObject(ir.getData());
        ctx.setParsedIntent(intent);
        if (intent != null && Boolean.TRUE.equals(intent.getBoolean("conflict"))) {
            ctx.setConflictDetected(true);
            ctx.setClarifyQuestion(intent.getString("clarify_question"));
            saveSession(ctx);
            return ctx;
        }

        runDownstream(ctx, asyncNutrition);
        return ctx;
    }

    /**
     * 多轮追问闭环: 用户对前一次的 clarify_question 给出回复后,
     * 沿用原 traceId 继续跑后续 Agent。
     */
    public AgentContext continueWithReply(String traceId, String userReply) {
        AgentContext ctx = loadSession(traceId);
        if (ctx == null) {
            throw new RuntimeException("会话已过期或不存在: " + traceId);
        }
        // 把用户回复合入意图,清除冲突标记
        JSONObject intent = ctx.getParsedIntent();
        if (intent == null) intent = new JSONObject();
        intent.put("user_reply", userReply);
        intent.put("conflict", false);
        ctx.setParsedIntent(intent);
        ctx.getClarifyHistory().add(
                new AgentContext.ClarifyTurn(ctx.getClarifyQuestion(), userReply));
        ctx.setConflictDetected(false);
        ctx.setClarifyQuestion(null);

        runDownstream(ctx, false);
        deleteSession(traceId);
        return ctx;
    }

    /** 检索 + 并行(营养+地理) + 整合 */
    private void runDownstream(AgentContext ctx, boolean asyncNutrition) {
        JSONObject intent = ctx.getParsedIntent();
        String userId = ctx.getUserId();

        // 2. 菜品检索
        String retrievalInput = "意图: " + (intent == null ? "{}" : intent.toJSONString())
                + (ctx.getClarifyHistory().isEmpty() ? "" :
                    "\n用户在追问中补充了: " + JSON.toJSONString(ctx.getClarifyHistory()))
                + "\n请你调用 dish_query 工具查询候选菜品。";
        AgentResult dr = dishRetrievalAgent.execute(ctx, retrievalInput);
        logService.recordAsync(ctx.getTraceId(), userId, retrievalInput, dr);
        if (ctx.getCandidates().isEmpty()) {
            log.warn("[trace={}] dish retrieval got 0 candidates, fallback", ctx.getTraceId());
            applyFallback(ctx);
            return;
        }

        // 3. 并行: 营养 + 地理
        boolean needsNutrition = intent != null && intent.containsKey("health")
                && intent.getJSONArray("health") != null && !intent.getJSONArray("health").isEmpty();
        boolean needsGeo = ctx.getUserLocation() != null && !ctx.getUserLocation().isBlank();

        CompletableFuture<AgentResult> gf = needsGeo
                ? CompletableFuture.supplyAsync(() -> {
                    String input = "用户坐标: " + ctx.getUserLocation()
                            + "\n请对ctx.candidates调用 distance_calc。";
                    AgentResult r = geoMatchAgent.execute(ctx, input);
                    logService.recordAsync(ctx.getTraceId(), userId, input, r);
                    return r;
                }, pool)
                : CompletableFuture.completedFuture(null);

        if (asyncNutrition && needsNutrition) {
            // 主流程不等营养, 先用地理 + 候选打分整合一版"快"结果
            try { gf.get(); } catch (Exception e) { log.warn("geo failed", e); }
            assembleFinalList(ctx);
            // 后台跑营养 + 重新整合 -> 写 Redis 等前端轮询
            pool.submit(() -> enrichWithNutrition(ctx, intent));
        } else {
            // 经典同步路径
            CompletableFuture<AgentResult> nf = needsNutrition
                    ? CompletableFuture.supplyAsync(() -> {
                        String input = "意图: " + intent.toJSONString();
                        AgentResult r = nutritionAgent.execute(ctx, input);
                        logService.recordAsync(ctx.getTraceId(), userId, input, r);
                        return r;
                    }, pool)
                    : CompletableFuture.completedFuture(null);
            try {
                CompletableFuture.allOf(nf, gf).get();
            } catch (Exception e) {
                log.warn("[trace={}] parallel agents failed", ctx.getTraceId(), e);
            }
            assembleFinalList(ctx);
        }
    }

    /**
     * 异步营养评估 + 重新整合,结果存 Redis 等前端轮询。
     */
    private void enrichWithNutrition(AgentContext ctx, JSONObject intent) {
        try {
            String input = "意图: " + intent.toJSONString();
            AgentResult nr = nutritionAgent.execute(ctx, input);
            logService.recordAsync(ctx.getTraceId(), ctx.getUserId(), input, nr);

            // 重新整合
            assembleFinalList(ctx);

            // 写入 Redis,前端 GET /agent/recommend/{trace}/enriched 拉取
            JSONArray arr = new JSONArray();
            for (RecommendVo v : ctx.getFinalRecommendations()) {
                JSONObject o = new JSONObject();
                o.put("id", v.getId());
                o.put("dishesName", v.getDishesName());
                o.put("image", v.getImage());
                o.put("shopName", v.getShopName());
                o.put("aiDescription", v.getAiDescription());
                o.put("tagName", v.getTagName());
                o.put("nutritionScore", ctx.getNutritionScores().get(v.getId()));
                arr.add(o);
            }
            JSONObject enriched = new JSONObject();
            enriched.put("traceId", ctx.getTraceId());
            enriched.put("items", arr);
            enriched.put("nutritionScores", ctx.getNutritionScores());
            redisTemplate.opsForValue().set(ENRICHED_PREFIX + ctx.getTraceId(),
                    enriched.toJSONString(), ENRICHED_TTL_MIN, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("[trace={}] async nutrition enrich failed", ctx.getTraceId(), e);
        }
    }

    /** 调结果整合 Agent + 兜底排序 + 截 topK,写入 ctx.finalRecommendations */
    private void assembleFinalList(AgentContext ctx) {
        String integrateInput = buildIntegrateInput(ctx);
        AgentResult ar = resultIntegrateAgent.execute(ctx, integrateInput);
        logService.recordAsync(ctx.getTraceId(), ctx.getUserId(), integrateInput, ar);

        Map<String, String> reasons = new HashMap<>();
        List<String> orderedIds = new ArrayList<>();
        if (ar.isSuccess()) {
            JSONObject obj = toJsonObject(ar.getData());
            if (obj != null && obj.containsKey("items")) {
                JSONArray arr = obj.getJSONArray("items");
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    String id = item.getString("dishId");
                    if (id == null) continue;
                    orderedIds.add(id);
                    reasons.put(id, item.getString("reason"));
                }
            }
        }
        if (orderedIds.isEmpty()) {
            ctx.getCandidates().sort(Comparator.comparingDouble(
                    (RecommendVo v) -> ctx.getNutritionScores().getOrDefault(v.getId(), 50.0)).reversed());
            for (RecommendVo v : ctx.getCandidates()) orderedIds.add(v.getId());
        }
        Map<String, RecommendVo> idx = new HashMap<>();
        for (RecommendVo v : ctx.getCandidates()) idx.put(v.getId(), v);
        List<RecommendVo> finalList = new ArrayList<>();
        for (String id : orderedIds) {
            RecommendVo v = idx.get(id);
            if (v == null) continue;
            String reason = reasons.get(id);
            if (reason != null) v.setAiDescription(reason);
            finalList.add(v);
            if (finalList.size() >= ctx.getTopK()) break;
        }
        ctx.setFinalRecommendations(finalList);
    }

    /** 取异步 enrich 后的结果。返回 null 表示尚未完成。 */
    public String getEnriched(String traceId) {
        try { return redisTemplate.opsForValue().get(ENRICHED_PREFIX + traceId); }
        catch (Exception e) { return null; }
    }

    // ---------- 会话存取 ----------

    /** 序列化关键字段到 Redis,等下一轮 continueWithReply 加载。 */
    private void saveSession(AgentContext ctx) {
        try {
            JSONObject snap = new JSONObject();
            snap.put("traceId", ctx.getTraceId());
            snap.put("userQuery", ctx.getUserQuery());
            snap.put("userId", ctx.getUserId());
            snap.put("userLocation", ctx.getUserLocation());
            snap.put("topK", ctx.getTopK());
            snap.put("parsedIntent", ctx.getParsedIntent());
            snap.put("clarifyQuestion", ctx.getClarifyQuestion());
            snap.put("clarifyHistory", JSON.toJSONString(ctx.getClarifyHistory()));
            redisTemplate.opsForValue().set(SESSION_PREFIX + ctx.getTraceId(),
                    snap.toJSONString(), SESSION_TTL_MIN, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("save session fail trace={}", ctx.getTraceId(), e);
        }
    }

    private AgentContext loadSession(String traceId) {
        try {
            String json = redisTemplate.opsForValue().get(SESSION_PREFIX + traceId);
            if (json == null) return null;
            JSONObject snap = JSON.parseObject(json);
            AgentContext ctx = new AgentContext();
            // traceId 是 final,无法直接 set,通过反射赋值
            try {
                java.lang.reflect.Field f = AgentContext.class.getDeclaredField("traceId");
                f.setAccessible(true);
                f.set(ctx, snap.getString("traceId"));
            } catch (Exception ignored) {}
            ctx.setUserQuery(snap.getString("userQuery"));
            ctx.setUserId(snap.getString("userId"));
            ctx.setUserLocation(snap.getString("userLocation"));
            ctx.setTopK(snap.getIntValue("topK"));
            ctx.setParsedIntent(snap.getJSONObject("parsedIntent"));
            ctx.setClarifyQuestion(snap.getString("clarifyQuestion"));
            String histJson = snap.getString("clarifyHistory");
            if (histJson != null) {
                ctx.setClarifyHistory(JSON.parseArray(histJson, AgentContext.ClarifyTurn.class));
            }
            return ctx;
        } catch (Exception e) {
            log.warn("load session fail trace={}", traceId, e);
            return null;
        }
    }

    private void deleteSession(String traceId) {
        try { redisTemplate.delete(SESSION_PREFIX + traceId); } catch (Exception ignored) {}
    }

    // ---------- helpers ----------

    private void applyFallback(AgentContext ctx) {
        try {
            RecommenderSystem rs = new RecommenderSystem();
            rs.setStrategy(fallbackStrategy);
            List<RecommendVo> vos = rs.recommendItems(ctx.getUserId(), ctx.getTopK());
            if (vos != null) ctx.setFinalRecommendations(vos);
        } catch (Exception e) {
            log.warn("fallback strategy failed", e);
        }
    }

    private String buildIntegrateInput(AgentContext ctx) {
        JSONObject brief = new JSONObject();
        brief.put("intent", ctx.getParsedIntent());
        JSONArray cands = new JSONArray();
        for (RecommendVo v : ctx.getCandidates()) {
            JSONObject o = new JSONObject();
            o.put("dishId", v.getId());
            o.put("name", v.getDishesName());
            o.put("shop", v.getShopName());
            o.put("tags", v.getTagName());
            o.put("nutritionScore", ctx.getNutritionScores().get(v.getId()));
            o.put("distance", ctx.getDistanceInfo().get(v.getId()));
            cands.add(o);
        }
        brief.put("candidates", cands);
        brief.put("topK", ctx.getTopK());
        if (!ctx.getClarifyHistory().isEmpty()) {
            brief.put("clarifyHistory", ctx.getClarifyHistory());
        }
        return "请基于以下信息生成最终推荐及推荐理由:\n" + brief.toJSONString();
    }

    private JSONObject toJsonObject(Object o) {
        if (o == null) return null;
        if (o instanceof JSONObject jo) return jo;
        try { return JSON.parseObject(JSON.toJSONString(o)); }
        catch (Exception e) { return null; }
    }

    @PreDestroy
    void shutdown() { pool.shutdownNow(); }
}
