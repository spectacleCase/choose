package com.choose.service.agent.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.choose.agent.pojos.DishNutrition;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.agent.core.AgentContext;
import com.choose.service.agent.nutrition.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 营养数据查询工具:从DB批量取出ctx.candidates对应的已有营养数据。
 */
@Component
@RequiredArgsConstructor
public class NutritionLookupTool implements Tool {

    private final NutritionService nutritionService;

    @Override
    public String name() { return "nutrition_lookup"; }

    @Override
    public String description() {
        return "从知识库查询候选菜品的已有营养数据。返回每条记录的热量/蛋白/脂肪等。";
    }

    @Override
    public String parameterSchema() {
        return "{} (无参,默认对ctx.candidates的全部菜品做查询)";
    }

    @Override
    public String execute(JSONObject params, AgentContext ctx) {
        List<Long> ids = new ArrayList<>();
        for (RecommendVo v : ctx.getCandidates()) {
            try { ids.add(Long.valueOf(v.getId())); } catch (Exception ignored) {}
        }
        List<DishNutrition> rows = nutritionService.listByDishIds(ids);
        JSONArray arr = new JSONArray();
        for (DishNutrition n : rows) {
            JSONObject o = new JSONObject();
            o.put("dishId", String.valueOf(n.getDishId()));
            o.put("calorie", n.getCalorie());
            o.put("fat", n.getFat());
            o.put("protein", n.getProtein());
            o.put("tags", n.getHealthTags());
            arr.add(o);
        }
        return "找到 " + rows.size() + " / " + ids.size() + " 条已有营养数据: " + arr.toJSONString();
    }
}
