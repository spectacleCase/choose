package com.choose.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.agent.pojos.DishNutrition;
import com.choose.service.agent.nutrition.NutritionService;
import com.choose.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 营养知识库管理(论文5.2 知识库管理模块)。
 */
@RestController
@RequestMapping("/choose/admin/agent/nutrition")
@RequiredArgsConstructor
public class NutritionAdminController {

    private final NutritionService nutritionService;

    @GetMapping("/page")
    public Result page(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "20") Integer size) {
        LambdaQueryWrapper<DishNutrition> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(DishNutrition::getHealthTags, keyword);
        }
        qw.orderByDesc(DishNutrition::getCreateTime)
          .last("LIMIT " + ((page - 1) * size) + "," + size);
        List<DishNutrition> rows = nutritionService.list(qw);
        return Result.ok(rows);
    }

    @GetMapping("/{dishId}")
    public Result get(@PathVariable Long dishId) {
        return Result.ok(nutritionService.getByDishId(dishId));
    }

    @PostMapping
    public Result save(@RequestBody DishNutrition n) {
        if (n.getSource() == null) n.setSource("manual");
        nutritionService.saveOrUpdate(n);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        nutritionService.removeById(id);
        return Result.ok();
    }
}
