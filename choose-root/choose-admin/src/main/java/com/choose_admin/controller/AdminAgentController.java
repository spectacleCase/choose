package com.choose_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.agent.dto.AgentRecommendDto;
import com.choose.agent.pojos.AgentCallLog;
import com.choose.agent.pojos.DishIngredient;
import com.choose.agent.pojos.DishNutrition;
import com.choose.agent.pojos.HealthTag;
import com.choose.agent.pojos.Ingredient;
import com.choose.agent.pojos.RecommendFeedback;
import com.choose.service.agent.AgentRecommendService;
import com.choose.service.agent.feedback.RecommendFeedbackService;
import com.choose.service.agent.health.HealthTagService;
import com.choose.service.agent.ingredient.DishIngredientService;
import com.choose.service.agent.ingredient.IngredientService;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.service.agent.log.AgentStatsService;
import com.choose.service.agent.nutrition.NutritionService;
import com.choose.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
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

    // ============================== 演练台 ==============================
    @PostMapping("/recommend")
    public Result recommend(@Valid @RequestBody AgentRecommendDto dto) {
        return Result.ok(agentRecommendService.recommend(dto));
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

    // ============================== 菜品-食材关联 =======================
    @GetMapping("/dish/{dishId}/ingredients")
    public Result dishIngredients(@PathVariable Long dishId) {
        return Result.ok(dishIngredientService.listByDishId(dishId));
    }

    @PostMapping("/dish/{dishId}/ingredients")
    public Result dishIngredientsSave(@PathVariable Long dishId,
                                      @RequestBody List<DishIngredient> rows) {
        dishIngredientService.replaceByDish(dishId, rows);
        return Result.ok();
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
