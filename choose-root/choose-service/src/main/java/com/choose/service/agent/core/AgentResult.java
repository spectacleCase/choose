package com.choose.service.agent.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent单次执行结果。data字段类型由各Agent自定义。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentResult {

    private boolean success;
    private String agentName;
    private Object data;
    private String errorMessage;
    private List<ReActStep> steps = new ArrayList<>();
    private long elapsedMs;

    public static AgentResult ok(String agentName, Object data, List<ReActStep> steps, long elapsedMs) {
        return new AgentResult(true, agentName, data, null, steps, elapsedMs);
    }

    public static AgentResult fail(String agentName, String error, List<ReActStep> steps, long elapsedMs) {
        return new AgentResult(false, agentName, null, error, steps, elapsedMs);
    }
}
