package com.choose.controller.agent;

import com.choose.agent.dto.AgentRecommendDto;
import com.choose.agent.pojos.AgentCallLog;
import com.choose.service.agent.AgentRecommendService;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 多Agent协作推荐入口。论文4/5章核心创新点的对外接口。
 */
@RestController
@RequestMapping("/choose/recommend/v1/agent")
@RequiredArgsConstructor
public class AgentRecommendController {

    private final AgentRecommendService agentRecommendService;
    private final AgentCallLogService logService;

    /** 多Agent协作推荐 */
    @PostMapping("/recommend")
    public Result recommend(@Valid @RequestBody AgentRecommendDto dto) {
        return Result.ok(agentRecommendService.recommend(dto));
    }

    /** 按链路ID取出全部Agent调用记录(管理端Agent监控页) */
    @GetMapping("/trace/{traceId}")
    public Result trace(@PathVariable String traceId) {
        return Result.ok(agentRecommendService.getTrace(traceId));
    }

    /** 最近调用列表 */
    @GetMapping("/calls/recent")
    public Result recent(@RequestParam(defaultValue = "50") Integer limit) {
        List<AgentCallLog> rows = logService.listRecent(limit);
        return Result.ok(rows);
    }
}
