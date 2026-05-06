package com.choose.service.agent.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.choose.service.agent.log.ToolCallLogService;
import com.choose.service.agent.tool.Tool;
import com.choose.service.agent.tool.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ReAct 模式 Agent 基类。子类提供 name / systemPrompt / allowedTools。
 *
 * 支持 P-ReAct (论文 2.2.4 Parallel ReAct):
 *   模型在调用工具时可以同时输出 next_thought (占位推理)。
 *   Base 把 tool.execute 和 LLM 的 next_thought 推理放进两个 future 并行跑,
 *   将工具 I/O 等待时间与 LLM 推理时间重叠,降低端到端延迟。
 *
 * 模型输出格式:
 *   调用工具 (经典):
 *     {"thought":"...","action":{"name":"...","params":{...}}}
 *   调用工具 (P-ReAct, 推荐):
 *     {"thought":"...","action":{...},"next_thought":"等工具返回时同步思考的内容"}
 *   给最终答案:
 *     {"thought":"...","final_answer":{...}}
 */
@Slf4j
public abstract class BaseAgent {

    @Autowired
    protected AgentLLMClient llmClient;

    @Autowired
    protected ToolRegistry toolRegistry;

    @Autowired
    protected ToolCallLogService toolCallLogService;

    /** P-ReAct 并行池: 同时跑工具调用 + 预备推理 LLM 调用 */
    private static final ExecutorService P_REACT_POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "p-react-" + System.nanoTime());
        t.setDaemon(true);
        return t;
    });

    /** 单次 next_thought LLM 调用上限 */
    private static final long NEXT_THOUGHT_TIMEOUT_MS = 15_000L;

    public abstract String name();

    protected abstract String systemPromptHeader();

    protected abstract List<String> allowedTools();

    /** 子类可关闭 P-ReAct (默认开启) */
    protected boolean supportsPReAct() { return true; }

    protected int maxIterations() { return 5; }

    public AgentResult execute(AgentContext ctx, String userInput) {
        long start = System.currentTimeMillis();
        List<ReActStep> steps = new ArrayList<>();

        String systemPrompt = systemPromptHeader() + "\n\n"
                + "你可以使用以下工具:\n"
                + toolRegistry.renderToolsDescription(allowedTools())
                + "\n# 输出格式 (严格 JSON,无任何额外文字)\n"
                + "调用工具 (经典 ReAct): {\"thought\":\"...\",\"action\":{\"name\":\"...\",\"params\":{...}}}\n"
                + "调用工具 + 占位推理 (推荐, P-ReAct,论文 2.2.4): "
                + "{\"thought\":\"...\",\"action\":{\"name\":\"...\",\"params\":{...}},"
                + "\"next_thought\":\"等工具返回时可以预想的事\"}\n"
                + "给出最终答案: {\"thought\":\"...\",\"final_answer\":{...}}\n"
                + "next_thought 字段建议: 推断工具结果可能是什么形态、想好下一步如果结果异常该怎么办、"
                + "或者基于当前已有的信息做一些不依赖本次工具结果的预备推理。简短(<= 60 字)。\n";

        StringBuilder convo = new StringBuilder();
        convo.append("用户输入: ").append(userInput).append("\n");

        Object finalAnswer = null;
        String errMsg = null;

        for (int i = 0; i < maxIterations(); i++) {
            long stepStart = System.currentTimeMillis();
            String raw;
            try {
                raw = llmClient.chat(systemPrompt, convo.toString());
            } catch (Exception e) {
                errMsg = "LLM调用失败: " + e.getMessage();
                log.warn("[{}] LLM call failed at iter {}", name(), i, e);
                break;
            }

            JSONObject obj = parseJson(raw);
            if (obj == null) {
                steps.add(ReActStep.of(ReActStep.Type.OBSERVATION,
                        "模型输出格式错误,要求重新输出JSON",
                        System.currentTimeMillis() - stepStart));
                convo.append("Observation: 上一次输出不是有效JSON,请严格按格式重试。\n");
                continue;
            }

            String thought = obj.getString("thought");
            if (thought != null) {
                steps.add(ReActStep.of(ReActStep.Type.THOUGHT, thought,
                        System.currentTimeMillis() - stepStart));
            }

            if (obj.containsKey("final_answer")) {
                finalAnswer = obj.get("final_answer");
                steps.add(ReActStep.of(ReActStep.Type.FINAL_ANSWER,
                        JSON.toJSONString(finalAnswer), 0));
                break;
            }

            JSONObject action = obj.getJSONObject("action");
            if (action == null) {
                errMsg = "模型未返回action或final_answer";
                break;
            }
            String toolName = action.getString("name");
            JSONObject params = action.getJSONObject("params");
            if (params == null) params = new JSONObject();

            steps.add(ReActStep.of(ReActStep.Type.ACTION,
                    "调用 " + toolName + " 参数=" + params.toJSONString(), 0));

            Tool tool = toolRegistry.get(toolName);
            if (tool == null || !allowedTools().contains(toolName)) {
                String obs = "工具 " + toolName + " 不可用,允许的工具: " + allowedTools();
                steps.add(ReActStep.of(ReActStep.Type.OBSERVATION, obs, 0));
                convo.append("Observation: ").append(obs).append("\n");
                toolCallLogService.recordAsync(ctx.getTraceId(), name(), toolName,
                        params, obs, 0L, "DENIED", "tool not authorized");
                continue;
            }

            String nextThought = obj.getString("next_thought");
            boolean useP = supportsPReAct() && nextThought != null && !nextThought.isBlank();

            ToolRunResult tr;
            if (useP) {
                tr = runWithPReAct(tool, params, ctx, thought, nextThought);
            } else {
                tr = runSerial(tool, params, ctx);
            }

            steps.add(ReActStep.of(ReActStep.Type.OBSERVATION, tr.observation, tr.toolMs));
            convo.append("Action: ").append(toolName).append(" ").append(params.toJSONString()).append("\n");
            convo.append("Observation: ").append(tr.observation).append("\n");

            if (tr.preThought != null) {
                steps.add(ReActStep.of(ReActStep.Type.PRE_THOUGHT, tr.preThought, tr.preThoughtMs));
                convo.append("PreThought (P-ReAct): ").append(tr.preThought).append("\n");
                long savedHere = (tr.toolMs + tr.preThoughtMs) - Math.max(tr.toolMs, tr.preThoughtMs);
                if (savedHere > 0) {
                    ctx.setPReActSavedMs(ctx.getPReActSavedMs() + savedHere);
                }
            }

            toolCallLogService.recordAsync(ctx.getTraceId(), name(), toolName,
                    params, tr.observation, tr.toolMs, tr.toolStatus, tr.toolErr);
        }

        long elapsed = System.currentTimeMillis() - start;
        ctx.addTrace(name(), steps);
        ctx.getAgentLatency().put(name(), elapsed);

        if (finalAnswer != null) {
            return AgentResult.ok(name(), finalAnswer, steps, elapsed);
        }
        if (errMsg == null) errMsg = "达到最大迭代次数仍未给出final_answer";
        return AgentResult.fail(name(), errMsg, steps, elapsed);
    }

    // ---------- 工具执行: 经典 / P-ReAct 两种路径 ----------

    private ToolRunResult runSerial(Tool tool, JSONObject params, AgentContext ctx) {
        ToolRunResult r = new ToolRunResult();
        long t0 = System.currentTimeMillis();
        try {
            r.observation = tool.execute(params, ctx);
            r.toolStatus = "SUCCESS";
        } catch (Exception e) {
            r.observation = "工具执行失败: " + e.getMessage();
            r.toolStatus = "FAIL";
            r.toolErr = e.getMessage();
            log.warn("[{}] tool {} failed", name(), tool.name(), e);
        }
        r.toolMs = System.currentTimeMillis() - t0;
        return r;
    }

    /**
     * P-ReAct 核心: 工具调用 + 预备推理 LLM 调用并行执行。
     * 端到端耗时 ≈ max(tool_ms, pre_thought_ms),节省 = (tool+pre) - max。
     */
    private ToolRunResult runWithPReAct(Tool tool, JSONObject params, AgentContext ctx,
                                        String mainThought, String nextThought) {
        ToolRunResult r = new ToolRunResult();
        long t0 = System.currentTimeMillis();

        CompletableFuture<String> toolF = CompletableFuture.supplyAsync(() -> {
            try { return tool.execute(params, ctx); }
            catch (Exception e) { throw new RuntimeException(e); }
        }, P_REACT_POOL);

        CompletableFuture<long[]> preF = CompletableFuture.supplyAsync(() -> {
            long s = System.currentTimeMillis();
            try {
                String pSys = "你正在 ReAct 循环中等待工具返回。基于 [主推理] 和 [next_thought 规划],"
                        + "做一次轻量预备推理: 只输出 1-2 句关键判断,不要 JSON,不要 final_answer,"
                        + "也不要重复 next_thought 内容。";
                String pUser = "[主推理] " + mainThought + "\n[next_thought] " + nextThought;
                String out = llmClient.chat(pSys, pUser);
                long ms = System.currentTimeMillis() - s;
                synchronized (r) {
                    r.preThought = out == null ? null : out.trim();
                    r.preThoughtMs = ms;
                }
                return new long[]{ms};
            } catch (Exception e) {
                long ms = System.currentTimeMillis() - s;
                synchronized (r) {
                    r.preThought = "(预备推理失败: " + e.getMessage() + ")";
                    r.preThoughtMs = ms;
                }
                return new long[]{ms};
            }
        }, P_REACT_POOL);

        try {
            r.observation = toolF.get(45, TimeUnit.SECONDS);
            r.toolStatus = "SUCCESS";
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            r.observation = "工具执行失败: " + cause.getMessage();
            r.toolStatus = "FAIL";
            r.toolErr = cause.getMessage();
            log.warn("[{}] tool {} failed (P-ReAct)", name(), tool.name(), cause);
        }
        r.toolMs = System.currentTimeMillis() - t0;

        // 预备推理: 已设上限,避免拖慢主流程
        try {
            preF.get(NEXT_THOUGHT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("pre-thought timeout/fail, ignored: {}", e.getMessage());
            synchronized (r) {
                if (r.preThought == null) r.preThought = "(预备推理超时,已跳过)";
            }
        }
        return r;
    }

    // ---------- helpers ----------

    private static class ToolRunResult {
        String observation;
        String toolStatus;
        String toolErr;
        long toolMs;
        String preThought;
        long preThoughtMs;
    }

    /** 鲁棒JSON解析:模型可能输出```json ... ```代码块或前后带杂文本 */
    protected JSONObject parseJson(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.trim();
        if (s.startsWith("```")) {
            int firstNl = s.indexOf('\n');
            if (firstNl > 0) s = s.substring(firstNl + 1);
            int endFence = s.lastIndexOf("```");
            if (endFence > 0) s = s.substring(0, endFence);
            s = s.trim();
        }
        int lb = s.indexOf('{');
        int rb = s.lastIndexOf('}');
        if (lb < 0 || rb <= lb) return null;
        try {
            return JSON.parseObject(s.substring(lb, rb + 1));
        } catch (Exception e) {
            return null;
        }
    }
}
