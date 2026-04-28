package com.choose.service.agent.log;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.AgentCallLog;
import com.choose.mapper.AgentCallLogMapper;
import com.choose.service.agent.core.AgentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent调用日志服务。落库异步,不阻塞主流程。
 */
@Service
@Slf4j
public class AgentCallLogService extends ServiceImpl<AgentCallLogMapper, AgentCallLog> {

    @Async
    public void recordAsync(String traceId, String userId, String input, AgentResult result) {
        try {
            AgentCallLog log = new AgentCallLog();
            log.setTraceId(traceId);
            log.setAgentName(result.getAgentName());
            log.setUserId(userId);
            log.setInput(safeTrunc(input, 4000));
            log.setOutput(safeTrunc(JSON.toJSONString(result.getData()), 4000));
            log.setSteps(safeTrunc(JSON.toJSONString(result.getSteps()), 8000));
            log.setElapsedMs(result.getElapsedMs());
            log.setStatus(result.isSuccess() ? "SUCCESS" : "FAIL");
            log.setErrorMessage(result.getErrorMessage());
            save(log);
        } catch (Exception e) {
            log.error("save agent call log fail", e);
        }
    }

    public List<AgentCallLog> listByTrace(String traceId) {
        return list(new LambdaQueryWrapper<AgentCallLog>()
                .eq(AgentCallLog::getTraceId, traceId)
                .orderByAsc(AgentCallLog::getCreateTime));
    }

    public List<AgentCallLog> listRecent(int limit) {
        return list(new LambdaQueryWrapper<AgentCallLog>()
                .orderByDesc(AgentCallLog::getCreateTime)
                .last("LIMIT " + Math.min(limit, 200)));
    }

    private static String safeTrunc(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
