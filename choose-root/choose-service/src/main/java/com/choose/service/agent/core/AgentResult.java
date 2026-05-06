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
    /** 是否被 PromptGuard 拦截 (论文 2.1.3 三层防护中的输入侧) */
    private boolean blocked;

    public static AgentResult ok(String agentName, Object data, List<ReActStep> steps, long elapsedMs) {
        return new AgentResult(true, agentName, data, null, steps, elapsedMs, false);
    }

    public static AgentResult fail(String agentName, String error, List<ReActStep> steps, long elapsedMs) {
        return new AgentResult(false, agentName, null, error, steps, elapsedMs, false);
    }

    public static AgentResult blocked(String agentName, String reason, List<ReActStep> steps, long elapsedMs) {
        AgentResult r = new AgentResult(false, agentName, null, reason, steps, elapsedMs, true);
        return r;
    }
}
