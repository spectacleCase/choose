package com.choose.service.agent.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.choose.agent.pojos.DishNutrition;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.agent.core.AgentContext;
import com.choose.service.agent.nutrition.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 营养估算工具:对没有DB数据的菜品,调用LLM按菜名估算并打分。
 * 评分写入 ctx.nutritionScores 供下游使用。
 */
@Component
@RequiredArgsConstructor
public class NutritionEstimateTool implements Tool {

    private final NutritionService nutritionService;

    @Override
    public String name() { return "nutrition_estimate"; }

    @Override
    public String description() {
        return "对候选菜品调用LLM估算营养成分并打分(0-100,分数越高越健康)。";
    }

    @Override
    public String parameterSchema() {
        return "{\"healthGoal\":\"用户健康目标如减脂/低盐/高蛋白\"}";
    }

    @Override
    public String execute(JSONObject params, AgentContext ctx) {
        String goal = params.getString("healthGoal");
        if (goal == null) goal = "general";

        JSONArray arr = new JSONArray();
        for (RecommendVo v : ctx.getCandidates()) {
            Long id;
            try { id = Long.valueOf(v.getId()); } catch (Exception e) { continue; }
            DishNutrition n = nutritionService.getByDishId(id);
            if (n == null || n.getCalorie() == null) {
                n = nutritionService.estimateByName(id, v.getDishesName());
            }
            double score = scoreFor(n, goal);
            ctx.getNutritionScores().put(v.getId(), score);
            JSONObject o = new JSONObject();
            o.put("dishId", v.getId());
            o.put("score", score);
            o.put("calorie", n.getCalorie());
            o.put("fat", n.getFat());
            o.put("protein", n.getProtein());
            arr.add(o);
        }
        return "已对 " + arr.size() + " 条菜品完成营养估算: " + arr.toJSONString();
    }

    private double scoreFor(DishNutrition n, String goal) {
        if (n == null || n.getCalorie() == null) return 50.0;
        double s = 60;
        double cal = n.getCalorie();
        double fat = n.getFat() == null ? 15 : n.getFat();
        double protein = n.getProtein() == null ? 10 : n.getProtein();
        if (goal.contains("减脂") || goal.contains("低脂")) {
            if (cal < 400) s += 15;
            if (fat < 12) s += 15;
            if (protein > 20) s += 10;
            if (cal > 700) s -= 20;
        } else if (goal.contains("高蛋白")) {
            s += Math.min(30, protein * 1.0);
        } else if (goal.contains("低盐")) {
            double sodium = n.getSodium() == null ? 800 : n.getSodium();
            if (sodium < 600) s += 20;
            if (sodium > 1500) s -= 20;
        } else {
            if (cal < 600) s += 5;
        }
        return Math.max(0, Math.min(100, s));
    }
}
