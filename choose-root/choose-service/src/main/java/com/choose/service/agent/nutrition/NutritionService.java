package com.choose.service.agent.nutrition;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.DishIngredient;
import com.choose.agent.pojos.DishNutrition;
import com.choose.agent.pojos.HealthTag;
import com.choose.agent.pojos.Ingredient;
import com.choose.mapper.DishNutritionMapper;
import com.choose.service.agent.cache.TwoLevelCache;
import com.choose.service.agent.core.AgentLLMClient;
import com.choose.service.agent.health.HealthTagService;
import com.choose.service.agent.ingredient.DishIngredientService;
import com.choose.service.agent.ingredient.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 营养数据服务,完整闭环:
 *   优先级: DB -> 食材加权计算 -> LLM 估算
 * 同时负责把营养结果用 SpEL 自动评估出健康标签。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NutritionService extends ServiceImpl<DishNutritionMapper, DishNutrition> {

    private final AgentLLMClient llmClient;
    private final TwoLevelCache cache;
    @Lazy @Autowired private DishIngredientService dishIngredientService;
    @Lazy @Autowired private IngredientService ingredientService;
    @Lazy @Autowired private HealthTagService healthTagService;

    private static final String CACHE_NS = "nutrition.llm";
    private static final SpelExpressionParser SPEL = new SpelExpressionParser();

    public DishNutrition getByDishId(Long dishId) {
        if (dishId == null) return null;
        return getOne(new LambdaQueryWrapper<DishNutrition>().eq(DishNutrition::getDishId, dishId));
    }

    public List<DishNutrition> listByDishIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return list(new LambdaQueryWrapper<DishNutrition>().in(DishNutrition::getDishId, ids));
    }

    /**
     * 按食材清单加权计算菜品营养。无食材数据返回 null。
     */
    public DishNutrition estimateByIngredients(Long dishId) {
        if (dishId == null) return null;
        List<DishIngredient> rows = dishIngredientService.listByDishId(dishId);
        if (rows == null || rows.isEmpty()) return null;

        List<Long> ingIds = rows.stream().map(DishIngredient::getIngredientId).distinct().toList();
        Map<Long, Ingredient> idx = new HashMap<>();
        for (Ingredient i : ingredientService.listByIds(ingIds)) idx.put(i.getId(), i);

        double cal = 0, pro = 0, fat = 0, carb = 0, fib = 0, sod = 0;
        boolean any = false;
        for (DishIngredient di : rows) {
            Ingredient ing = idx.get(di.getIngredientId());
            if (ing == null || di.getAmount() == null || di.getAmount() <= 0) continue;
            double k = di.getAmount() / 100.0;
            cal  += zero(ing.getCalorie()) * k;
            pro  += zero(ing.getProtein()) * k;
            fat  += zero(ing.getFat()) * k;
            carb += zero(ing.getCarbs()) * k;
            fib  += zero(ing.getFiber()) * k;
            sod  += zero(ing.getSodium()) * k;
            any = true;
        }
        if (!any) return null;

        DishNutrition n = new DishNutrition();
        n.setDishId(dishId);
        n.setCalorie(round1(cal));
        n.setProtein(round1(pro));
        n.setFat(round1(fat));
        n.setCarbs(round1(carb));
        n.setFiber(round1(fib));
        n.setSodium(round1(sod));
        n.setSource("ingredient_calc");
        n.setHealthTags(JSONArray.toJSONString(evaluateTags(n)));
        return n;
    }

    /**
     * 用 SpEL 评估出该营养记录命中的健康标签名列表。
     * 标签 definition 形如:  calorie < 400 && fat < 12 && protein > 15
     */
    public List<String> evaluateTags(DishNutrition n) {
        List<String> hit = new ArrayList<>();
        if (n == null) return hit;
        List<HealthTag> tags = healthTagService.list(
                new LambdaQueryWrapper<HealthTag>().eq(HealthTag::getStatus, 1));
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("calorie", zero(n.getCalorie()));
        ctx.setVariable("protein", zero(n.getProtein()));
        ctx.setVariable("fat",     zero(n.getFat()));
        ctx.setVariable("carbs",   zero(n.getCarbs()));
        ctx.setVariable("fiber",   zero(n.getFiber()));
        ctx.setVariable("sodium",  zero(n.getSodium()));
        for (HealthTag t : tags) {
            String def = t.getDefinition();
            if (def == null || def.isBlank()) continue;
            String expr = def.replace("calorie", "#calorie")
                             .replace("protein", "#protein")
                             .replace("fat",     "#fat")
                             .replace("carbs",   "#carbs")
                             .replace("fiber",   "#fiber")
                             .replace("sodium",  "#sodium");
            try {
                Expression e = SPEL.parseExpression(expr);
                Boolean v = e.getValue(ctx, Boolean.class);
                if (Boolean.TRUE.equals(v)) hit.add(t.getName());
            } catch (Exception ex) {
                log.debug("tag '{}' SpEL parse fail: {}", t.getName(), ex.getMessage());
            }
        }
        return hit;
    }

    /**
     * 对所有已有营养记录重新评估健康标签 (论文 5.2 "批量计算" 路径)。
     */
    public int batchClassify() {
        List<DishNutrition> all = list();
        int updated = 0;
        for (DishNutrition n : all) {
            String old = n.getHealthTags();
            List<String> tags = evaluateTags(n);
            String now = JSONArray.toJSONString(tags);
            if (!now.equals(old)) {
                n.setHealthTags(now);
                updateById(n);
                updated++;
            }
        }
        return updated;
    }

    /**
     * 让LLM按菜名估算营养。走二级缓存 (论文 5.1):
     * 同一菜名近 60 分钟内重复调直接命中 L1/L2,避免重复调 LLM。
     */
    public DishNutrition estimateByName(Long dishId, String dishName) {
        DishNutrition n;
        try {
            n = cache.get(CACHE_NS, dishName, DishNutrition.class, k -> {
                String sys = "你是营养师。基于中国餐饮常见做法,估算给定菜品一份的营养成分。" +
                        "严格输出JSON: {\"calorie\":kcal,\"protein\":g,\"fat\":g,\"carbs\":g,\"fiber\":g,\"sodium\":mg}";
                try {
                    String raw = llmClient.chat(sys, "菜品名: " + k);
                    DishNutrition parsed = parseLlmJson(raw);
                    return parsed != null ? parsed : fallback();
                } catch (Exception e) {
                    log.warn("LLM estimate failed for {}", k, e);
                    return fallback();
                }
            });
        } catch (Exception e) {
            n = fallback();
        }
        if (n == null) n = fallback();
        n.setDishId(dishId);
        n.setSource("llm_estimate");
        n.setHealthTags(JSONArray.toJSONString(evaluateTags(n)));
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
        return n;
    }

    private static double zero(Double v) { return v == null ? 0.0 : v; }
    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
}
