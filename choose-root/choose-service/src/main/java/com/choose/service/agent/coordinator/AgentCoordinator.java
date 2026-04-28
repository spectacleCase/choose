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
import com.choose.service.recommend.RecommenderSystem;
import com.choose.service.recommend.Impl.CascadeHybridRecommendationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Agent协调器。论文4.2/5.1的多Agent协作流程控制器。
 * 执行顺序: IntentParse -> DishRetrieval -> 并行(Nutrition, GeoMatch) -> ResultIntegrate
 * 任一环节失败则降级到原有协同过滤策略。
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

    /** 营养评估和地理匹配的并行池 */
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public AgentContext run(String userQuery, String userId, String userLocation, int topK) {
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
        }

        // 2. 菜品检索
        String retrievalInput = "意图: " + (intent == null ? "{}" : intent.toJSONString())
                + "\n请你调用 dish_query 工具查询候选菜品。";
        AgentResult dr = dishRetrievalAgent.execute(ctx, retrievalInput);
        logService.recordAsync(ctx.getTraceId(), userId, retrievalInput, dr);
        if (ctx.getCandidates().isEmpty()) {
            log.warn("[trace={}] dish retrieval got 0 candidates, fallback", ctx.getTraceId());
            applyFallback(ctx);
            return ctx;
        }

        // 3. 并行: 营养 + 地理
        boolean needsNutrition = intent != null && intent.containsKey("health")
                && intent.getJSONArray("health") != null && !intent.getJSONArray("health").isEmpty();
        boolean needsGeo = userLocation != null && !userLocation.isBlank();

        CompletableFuture<AgentResult> nf = needsNutrition
                ? CompletableFuture.supplyAsync(() -> {
                    String input = "意图: " + intent.toJSONString();
                    AgentResult r = nutritionAgent.execute(ctx, input);
                    logService.recordAsync(ctx.getTraceId(), userId, input, r);
                    return r;
                }, pool)
                : CompletableFuture.completedFuture(null);

        CompletableFuture<AgentResult> gf = needsGeo
                ? CompletableFuture.supplyAsync(() -> {
                    String input = "用户坐标: " + userLocation
                            + "\n请对ctx.candidates调用 distance_calc。";
                    AgentResult r = geoMatchAgent.execute(ctx, input);
                    logService.recordAsync(ctx.getTraceId(), userId, input, r);
                    return r;
                }, pool)
                : CompletableFuture.completedFuture(null);

        try {
            CompletableFuture.allOf(nf, gf).get();
        } catch (Exception e) {
            log.warn("[trace={}] parallel agents failed", ctx.getTraceId(), e);
        }

        // 4. 结果整合
        String integrateInput = buildIntegrateInput(ctx);
        AgentResult ar = resultIntegrateAgent.execute(ctx, integrateInput);
        logService.recordAsync(ctx.getTraceId(), userId, integrateInput, ar);

        // 解析最终推荐顺序与理由
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
        // 兜底: 没拿到顺序就按营养分+距离打分排
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
            if (finalList.size() >= topK) break;
        }
        ctx.setFinalRecommendations(finalList);
        return ctx;
    }

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
        return "请基于以下信息生成最终推荐及推荐理由:\n" + brief.toJSONString();
    }

    private JSONObject toJsonObject(Object o) {
        if (o == null) return null;
        if (o instanceof JSONObject jo) return jo;
        try { return JSON.parseObject(JSON.toJSONString(o)); }
        catch (Exception e) { return null; }
    }
}
