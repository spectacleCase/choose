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

import java.util.Date;
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
            // 简易 token 估算: input/output 字符数 / 2
            long tk = (input == null ? 0 : input.length() / 2);
            if (result.getData() != null) tk += JSON.toJSONString(result.getData()).length() / 2;
            log.setTokenUsage(tk);
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

    /**
     * 带筛选条件的列表查询。所有参数都是可空(null=不筛选)。
     */
    public List<AgentCallLog> listFiltered(Date from, Date to, String agentName,
                                           String status, int limit) {
        LambdaQueryWrapper<AgentCallLog> qw = new LambdaQueryWrapper<>();
        if (from != null) qw.ge(AgentCallLog::getCreateTime, from);
        if (to != null) qw.le(AgentCallLog::getCreateTime, to);
        if (agentName != null && !agentName.isBlank()) qw.eq(AgentCallLog::getAgentName, agentName);
        if (status != null && !status.isBlank()) qw.eq(AgentCallLog::getStatus, status);
        qw.orderByDesc(AgentCallLog::getCreateTime).last("LIMIT " + Math.min(limit, 500));
        return list(qw);
    }

    private static String safeTrunc(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
