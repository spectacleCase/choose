package com.choose_admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.agent.dto.AgentContinueDto;
import com.choose.agent.dto.AgentRecommendDto;
import com.choose.agent.pojos.AgentCallLog;
import com.choose.agent.pojos.DishIngredient;
import com.choose.agent.pojos.DishNutrition;
import com.choose.agent.pojos.HealthTag;
import com.choose.agent.pojos.Ingredient;
import com.choose.agent.pojos.RecommendFeedback;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.mapper.DishesMapper;
import com.choose.mapper.ShopsMapper;
import com.choose.service.agent.AgentRecommendService;
import com.choose.service.agent.cache.TwoLevelCache;
import com.choose.service.agent.core.AgentLLMClient;
import com.choose.service.agent.feedback.RecommendFeedbackService;
import com.choose.service.agent.health.HealthTagService;
import com.choose.service.agent.ingredient.DishIngredientService;
import com.choose.service.agent.ingredient.IngredientService;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.service.agent.log.AgentStatsService;
import com.choose.service.agent.log.ToolCallLogService;
import com.choose.service.agent.nutrition.NutritionService;
import com.choose.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端 Agent 协作模块统一入口。前缀 /choose-admin 与 vite 代理对齐。
 * 涵盖: 推荐演练 / 调用监控 + 筛选 / 聚合统计 / 知识库(营养、健康标签、食材) / 满意度反馈。
 */
@RestController
@RequestMapping("/choose-admin/agent")
@RequiredArgsConstructor
public class AdminAgentController {

    private final AgentRecommendService agentRecommendService;
    private final AgentCallLogService logService;
    private final AgentStatsService statsService;
    private final NutritionService nutritionService;
    private final HealthTagService healthTagService;
    private final IngredientService ingredientService;
    private final DishIngredientService dishIngredientService;
    private final RecommendFeedbackService feedbackService;
    private final DishesMapper dishesMapper;
    private final ShopsMapper shopsMapper;
    private final AgentLLMClient llmClient;
    private final ToolCallLogService toolCallLogService;
    private final TwoLevelCache twoLevelCache;

    // ============================== 推荐入口 (供管理端测试 / 内部调用) ===========
    @PostMapping("/recommend")
    public Result recommend(@Valid @RequestBody AgentRecommendDto dto) {
        return Result.ok(agentRecommendService.recommend(dto));
    }

    /** 多轮追问闭环: 提交用户对前一轮 clarify_question 的回复。 */
    @PostMapping("/continue")
    public Result continueWithReply(@Valid @RequestBody AgentContinueDto dto) {
        return Result.ok(agentRecommendService.continueWithReply(dto.getTraceId(), dto.getReply()));
    }

    /**
     * 异步营养评估完成后,前端轮询此接口拿增量结果。
     * 未完成返回 status=PENDING; 完成返回 status=READY + items。
     */
    @GetMapping("/recommend/{traceId}/enriched")
    public Result enriched(@PathVariable String traceId) {
        String json = agentRecommendService.getEnriched(traceId);
        if (json == null) {
            return Result.ok(java.util.Map.of("status", "PENDING"));
        }
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("status", "READY");
        m.put("data", com.alibaba.fastjson.JSON.parseObject(json));
        return Result.ok(m);
    }

    /** 二级缓存命中率统计 (论文 5.1) */
    @GetMapping("/cache/stats")
    public Result cacheStats() {
        return Result.ok(twoLevelCache.stats());
    }

    // ============================== 监控 ================================
    @GetMapping("/calls/recent")
    public Result recent(@RequestParam(defaultValue = "50") Integer limit) {
        return Result.ok(logService.listRecent(limit));
    }

    /** 带筛选的列表查询 */
    @GetMapping("/calls/filter")
    public Result filter(@RequestParam(required = false)
                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
                         @RequestParam(required = false)
                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to,
                         @RequestParam(required = false) String agentName,
                         @RequestParam(required = false) String status,
                         @RequestParam(defaultValue = "200") Integer limit) {
        return Result.ok(logService.listFiltered(from, to, agentName, status, limit));
    }

    @GetMapping("/trace/{traceId}")
    public Result trace(@PathVariable String traceId) {
        return Result.ok(agentRecommendService.getTrace(traceId));
    }

    /** 工具级调用记录: 按链路或最近列表 */
    @GetMapping("/tool-calls/by-trace/{traceId}")
    public Result toolCallsByTrace(@PathVariable String traceId) {
        return Result.ok(toolCallLogService.listByTrace(traceId));
    }

    @GetMapping("/tool-calls/recent")
    public Result toolCallsRecent(@RequestParam(defaultValue = "100") Integer limit) {
        return Result.ok(toolCallLogService.listRecent(limit));
    }

    /** LLM 熔断器状态 */
    @GetMapping("/llm/breaker")
    public Result llmBreaker() {
        return Result.ok(llmClient.breakerStatus());
    }

    // ============================== 聚合统计 (4 类指标) ==================
    @GetMapping("/stats/overview")
    public Result statsOverview(@RequestParam(required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
                                @RequestParam(required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to) {
        return Result.ok(statsService.overview(from, to));
    }

    // ============================== 推荐效果分析看板 =====================
    @GetMapping("/effect/dashboard")
    public Result effectDashboard(@RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to) {
        return Result.ok(Map.of(
                "dailyRequestCount", statsService.dailyRequestCount(from, to),
                "dailyAvgResponseTime", statsService.dailyAvgResponseTime(from, to),
                "ratingDistribution", feedbackService.ratingDistribution(from, to)
        ));
    }

    // ============================== 营养 CRUD ===========================
    @GetMapping("/nutrition/page")
    public Result nutritionPage(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        LambdaQueryWrapper<DishNutrition> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) qw.like(DishNutrition::getHealthTags, keyword);
        int offset = Math.max(0, (page - 1)) * size;
        qw.orderByDesc(DishNutrition::getCreateTime).last("LIMIT " + offset + "," + size);
        long total = nutritionService.count();
        return Result.ok(Map.of("list", nutritionService.list(qw), "total", total));
    }

    @PostMapping("/nutrition")
    public Result nutritionSave(@RequestBody DishNutrition n) {
        if (n.getSource() == null) n.setSource("manual");
        nutritionService.saveOrUpdate(n);
        return Result.ok();
    }

    @DeleteMapping("/nutrition/{id}")
    public Result nutritionDelete(@PathVariable Long id) {
        nutritionService.removeById(id);
        return Result.ok();
    }

    // ============================== 健康标签 CRUD =======================
    @GetMapping("/health-tag/list")
    public Result healthTagList(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<HealthTag> qw = new LambdaQueryWrapper<>();
        if (status != null) qw.eq(HealthTag::getStatus, status);
        qw.orderByAsc(HealthTag::getId);
        return Result.ok(healthTagService.list(qw));
    }

    @PostMapping("/health-tag")
    public Result healthTagSave(@RequestBody HealthTag tag) {
        if (tag.getStatus() == null) tag.setStatus(1);
        healthTagService.saveOrUpdate(tag);
        return Result.ok();
    }

    @PostMapping("/health-tag/{id}/toggle")
    public Result healthTagToggle(@PathVariable Long id) {
        HealthTag t = healthTagService.getById(id);
        if (t == null) return Result.error("标签不存在");
        t.setStatus(t.getStatus() != null && t.getStatus() == 1 ? 0 : 1);
        healthTagService.updateById(t);
        return Result.ok();
    }

    @DeleteMapping("/health-tag/{id}")
    public Result healthTagDelete(@PathVariable Long id) {
        healthTagService.removeById(id);
        return Result.ok();
    }

    // ============================== 食材 CRUD ===========================
    @GetMapping("/ingredient/page")
    public Result ingredientPage(@RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "20") Integer size) {
        LambdaQueryWrapper<Ingredient> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(Ingredient::getName, keyword).or().like(Ingredient::getCategory, keyword));
        }
        int offset = Math.max(0, (page - 1)) * size;
        qw.orderByDesc(Ingredient::getCreateTime).last("LIMIT " + offset + "," + size);
        return Result.ok(Map.of("list", ingredientService.list(qw),
                "total", ingredientService.count()));
    }

    @PostMapping("/ingredient")
    public Result ingredientSave(@RequestBody Ingredient i) {
        ingredientService.saveOrUpdate(i);
        return Result.ok();
    }

    @DeleteMapping("/ingredient/{id}")
    public Result ingredientDelete(@PathVariable Long id) {
        ingredientService.removeById(id);
        return Result.ok();
    }

    // ============================== 菜品 检索 / 详情 ====================
    /** 模糊搜索菜品 (供菜品-食材编辑页选菜用) */
    @GetMapping("/dish/search")
    public Result dishSearch(@RequestParam(required = false) String keyword,
                             @RequestParam(defaultValue = "20") Integer limit) {
        LambdaQueryWrapper<Dishes> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(Dishes::getDishesName, keyword);
        }
        qw.last("LIMIT " + Math.min(limit, 50));
        List<Dishes> rows = dishesMapper.selectList(qw);
        // 附带店铺名,前端展示用
        List<Map<String, Object>> ret = new ArrayList<>();
        for (Dishes d : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("dishesName", d.getDishesName());
            m.put("image", d.getImage());
            m.put("mark", d.getMark());
            m.put("isAudit", d.getIsAudit());
            if (d.getShop() != null) {
                Shops s = shopsMapper.selectById(d.getShop());
                if (s != null) m.put("shopName", s.getShopName());
            }
            ret.add(m);
        }
        return Result.ok(ret);
    }

    // ============================== 菜品-食材关联 =======================
    @GetMapping("/dish/{dishId}/ingredients")
    public Result dishIngredients(@PathVariable Long dishId) {
        List<DishIngredient> rows = dishIngredientService.listByDishId(dishId);
        // 一并返回食材主数据,省一次往返
        List<Long> ids = rows.stream().map(DishIngredient::getIngredientId).distinct().toList();
        Map<Long, Ingredient> idx = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Ingredient i : ingredientService.listByIds(ids)) idx.put(i.getId(), i);
        }
        List<Map<String, Object>> ret = new ArrayList<>();
        for (DishIngredient r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("dishId", r.getDishId());
            m.put("ingredientId", r.getIngredientId());
            m.put("amount", r.getAmount());
            Ingredient ing = idx.get(r.getIngredientId());
            if (ing != null) {
                m.put("ingredientName", ing.getName());
                m.put("category", ing.getCategory());
            }
            ret.add(m);
        }
        return Result.ok(ret);
    }

    @PostMapping("/dish/{dishId}/ingredients")
    public Result dishIngredientsSave(@PathVariable Long dishId,
                                      @RequestBody List<DishIngredient> rows) {
        dishIngredientService.replaceByDish(dishId, rows);
        return Result.ok();
    }

    /** 基于当前食材清单预览营养(不入库)。前端实时显示用。 */
    @GetMapping("/dish/{dishId}/nutrition-preview")
    public Result nutritionPreview(@PathVariable Long dishId) {
        DishNutrition n = nutritionService.estimateByIngredients(dishId);
        if (n == null) return Result.ok(Map.of("hasIngredients", false));
        Map<String, Object> m = new HashMap<>();
        m.put("hasIngredients", true);
        m.put("nutrition", n);
        m.put("matchedTags", JSONArray.parse(n.getHealthTags()));
        return Result.ok(m);
    }

    /**
     * 保存食材清单 + 自动按食材加权计算营养并落库到 dish_nutrition,
     * 同时按健康标签 SpEL 表达式自动分类。
     * 整条链路一步完成,论文 5.2 食材成分管理的"全闭环"。
     */
    @PostMapping("/dish/{dishId}/ingredients/save-and-compute")
    public Result saveIngredientsAndCompute(@PathVariable Long dishId,
                                            @RequestBody List<DishIngredient> rows) {
        dishIngredientService.replaceByDish(dishId, rows);
        DishNutrition n = nutritionService.estimateByIngredients(dishId);
        if (n == null) {
            return Result.ok(Map.of("computed", false,
                    "message", "未能从食材计算营养,请检查食材数据完整性"));
        }
        // upsert
        DishNutrition existing = nutritionService.getByDishId(dishId);
        if (existing != null) {
            n.setId(existing.getId());
        }
        nutritionService.saveOrUpdate(n);
        return Result.ok(Map.of(
                "computed", true,
                "nutrition", n,
                "matchedTags", JSONArray.parse(n.getHealthTags())
        ));
    }

    /**
     * 批量重算所有 dish_nutrition 记录的健康标签 (论文 5.2 "批量计算"路径)。
     * 不重算营养值,只按当前 HealthTag 定义重新评估命中标签。
     */
    @PostMapping("/nutrition/batch-classify")
    public Result batchClassify() {
        int updated = nutritionService.batchClassify();
        return Result.ok(Map.of("updated", updated));
    }

    // ============================== 满意度反馈 ==========================
    @PostMapping("/feedback")
    public Result saveFeedback(@RequestBody RecommendFeedback fb) {
        if (fb.getRating() == null || fb.getRating() < 1 || fb.getRating() > 5) {
            return Result.error("rating 必须在 1-5");
        }
        feedbackService.save(fb);
        return Result.ok();
    }
}
