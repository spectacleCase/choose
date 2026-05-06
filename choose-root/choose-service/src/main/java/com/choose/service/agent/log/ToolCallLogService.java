package com.choose.service.agent.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.agent.pojos.ToolCallLog;
import com.choose.mapper.ToolCallLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工具调用日志服务。论文 4.3 "统一监控" 原则的落地。
 */
@Service
@Slf4j
public class ToolCallLogService extends ServiceImpl<ToolCallLogMapper, ToolCallLog> {

    @Async
    public void recordAsync(String traceId, String agentName, String toolName,
                            JSONObject params, String observation, long elapsedMs,
                            String status, String errorMessage) {
        try {
            ToolCallLog row = new ToolCallLog();
            row.setTraceId(traceId);
            row.setAgentName(agentName);
            row.setToolName(toolName);
            row.setParams(params == null ? null : params.toJSONString());
            row.setObservation(safeTrunc(observation, 4000));
            row.setElapsedMs(elapsedMs);
            row.setStatus(status);
            row.setErrorMessage(safeTrunc(errorMessage, 1000));
            save(row);
        } catch (Exception e) {
            log.warn("save tool call log fail", e);
        }
    }

    public List<ToolCallLog> listByTrace(String traceId) {
        return list(new LambdaQueryWrapper<ToolCallLog>()
                .eq(ToolCallLog::getTraceId, traceId)
                .orderByAsc(ToolCallLog::getCreateTime));
    }

    public List<ToolCallLog> listRecent(int limit) {
        return list(new LambdaQueryWrapper<ToolCallLog>()
                .orderByDesc(ToolCallLog::getCreateTime)
                .last("LIMIT " + Math.min(limit, 200)));
    }

    private static String safeTrunc(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
