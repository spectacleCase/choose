package com.choose_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.agent.dto.AgentRecommendDto;
import com.choose.agent.pojos.AgentCallLog;
import com.choose.agent.pojos.DishNutrition;
import com.choose.service.agent.AgentRecommendService;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.service.agent.nutrition.NutritionService;
import com.choose.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端 Agent 协作模块统一入口。
 * 路径前缀 /choose-admin 与现有管理端 vite 代理(http://localhost:12150/choose-admin)对齐。
 */
@RestController
@RequestMapping("/choose-admin/agent")
@RequiredArgsConstructor
public class AdminAgentController {

    private final AgentRecommendService agentRecommendService;
    private final AgentCallLogService logService;
    private final NutritionService nutritionService;

    // ============== 演练台: 管理员可在后台直接试 Agent 推荐 ==============
    @PostMapping("/recommend")
    public Result recommend(@Valid @RequestBody AgentRecommendDto dto) {
        return Result.ok(agentRecommendService.recommend(dto));
    }

    // ============== 监控: 调用列表 / 链路 trace ==============
    @GetMapping("/calls/recent")
    public Result recent(@RequestParam(defaultValue = "50") Integer limit) {
        List<AgentCallLog> rows = logService.listRecent(limit);
        return Result.ok(rows);
    }

    @GetMapping("/trace/{traceId}")
    public Result trace(@PathVariable String traceId) {
        return Result.ok(agentRecommendService.getTrace(traceId));
    }

    // ============== 营养知识库 CRUD ==============
    @GetMapping("/nutrition/page")
    public Result nutritionPage(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        LambdaQueryWrapper<DishNutrition> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(DishNutrition::getHealthTags, keyword);
        }
        int offset = Math.max(0, (page - 1)) * size;
        qw.orderByDesc(DishNutrition::getCreateTime)
          .last("LIMIT " + offset + "," + size);
        List<DishNutrition> rows = nutritionService.list(qw);
        long total = nutritionService.count();
        return Result.ok(java.util.Map.of("list", rows, "total", total));
    }

    @GetMapping("/nutrition/{dishId}")
    public Result nutritionGet(@PathVariable Long dishId) {
        return Result.ok(nutritionService.getByDishId(dishId));
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
}
