package com.choose.service.agent;

import com.choose.agent.dto.AgentRecommendDto;
import com.choose.agent.vo.AgentRecommendVo;
import com.choose.agent.vo.AgentTraceVo;
import com.choose.config.UserLocalThread;
import com.choose.service.agent.coordinator.AgentCoordinator;
import com.choose.service.agent.core.AgentContext;
import com.choose.service.agent.log.AgentCallLogService;
import com.choose.user.pojos.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Agent协作推荐对外服务门面。
 */
@Service
@RequiredArgsConstructor
public class AgentRecommendService {

    private final AgentCoordinator coordinator;
    private final AgentCallLogService logService;

    public AgentRecommendVo recommend(AgentRecommendDto dto) {
        String userId = null;
        UserInfo u = UserLocalThread.getUser();
        if (u != null && u.getId() != null) userId = String.valueOf(u.getId());

        int topK = dto.getNum() == null || dto.getNum() <= 0 ? 5 : dto.getNum();
        boolean async = Boolean.TRUE.equals(dto.getAsyncNutrition());
        AgentContext ctx = coordinator.run(dto.getQuery(), userId, dto.getLocation(), topK, async);

        AgentRecommendVo vo = new AgentRecommendVo();
        vo.setTraceId(ctx.getTraceId());
        vo.setConflictDetected(ctx.isConflictDetected());
        vo.setClarifyQuestion(ctx.getClarifyQuestion());
        vo.setParsedIntent(ctx.getParsedIntent());
        vo.setItems(ctx.getFinalRecommendations());
        vo.setAgentLatency(ctx.getAgentLatency());
        vo.setAsyncNutritionPending(async);
        vo.setPReActSavedMs(ctx.getPReActSavedMs());
        vo.setBlocked(ctx.isBlocked());
        vo.setBlockReason(ctx.getBlockReason());
        return vo;
    }

    /** 取异步营养完成后的增量结果 (论文 3.2 "异步计算后补充展示") */
    public String getEnriched(String traceId) {
        return coordinator.getEnriched(traceId);
    }

    public AgentTraceVo getTrace(String traceId) {
        AgentTraceVo vo = new AgentTraceVo();
        vo.setTraceId(traceId);
        vo.setCalls(logService.listByTrace(traceId));
        return vo;
    }

    /**
     * 多轮追问闭环: 用户回复 clarify_question 后,沿用原 traceId 继续推理。
     */
    public AgentRecommendVo continueWithReply(String traceId, String userReply) {
        AgentContext ctx = coordinator.continueWithReply(traceId, userReply);
        AgentRecommendVo vo = new AgentRecommendVo();
        vo.setTraceId(ctx.getTraceId());
        vo.setConflictDetected(ctx.isConflictDetected());
        vo.setClarifyQuestion(ctx.getClarifyQuestion());
        vo.setParsedIntent(ctx.getParsedIntent());
        vo.setItems(ctx.getFinalRecommendations());
        vo.setAgentLatency(ctx.getAgentLatency());
        vo.setPReActSavedMs(ctx.getPReActSavedMs());
        return vo;
    }
}
