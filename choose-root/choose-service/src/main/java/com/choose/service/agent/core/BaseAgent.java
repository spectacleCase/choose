package com.choose.service.agent.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.choose.service.agent.tool.Tool;
import com.choose.service.agent.tool.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * ReAct模式Agent基类。子类提供name、systemPrompt、allowedTools。
 * 模型需要按以下JSON格式输出:
 *   {"thought":"...","action":{"name":"toolX","params":{...}}}
 *   或
 *   {"thought":"...","final_answer":{...}}
 */
@Slf4j
public abstract class BaseAgent {

    @Autowired
    protected AgentLLMClient llmClient;

    @Autowired
    protected ToolRegistry toolRegistry;

    /** 子类决定 */
    public abstract String name();

    /** 子类提供该Agent的角色定义+任务说明 */
    protected abstract String systemPromptHeader();

    /** 子类声明本Agent能调用的工具 */
    protected abstract List<String> allowedTools();

    /** 单次推理最大循环数,防止无限调工具 */
    protected int maxIterations() { return 5; }

    /**
     * 执行Agent。失败时返回 fail 结果,不抛异常。
     */
    public AgentResult execute(AgentContext ctx, String userInput) {
        long start = System.currentTimeMillis();
        List<ReActStep> steps = new ArrayList<>();

        String systemPrompt = systemPromptHeader() + "\n\n"
                + "你可以使用以下工具:\n"
                + toolRegistry.renderToolsDescription(allowedTools())
                + "\n你必须严格按照以下JSON之一回复, 不要输出任何其他文字:\n"
                + "调用工具: {\"thought\":\"思考内容\",\"action\":{\"name\":\"工具名\",\"params\":{...}}}\n"
                + "给出最终答案: {\"thought\":\"思考内容\",\"final_answer\":{...}}\n";

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
                continue;
            }

            String observation;
            try {
                observation = tool.execute(params, ctx);
            } catch (Exception e) {
                observation = "工具执行失败: " + e.getMessage();
                log.warn("[{}] tool {} failed", name(), toolName, e);
            }
            steps.add(ReActStep.of(ReActStep.Type.OBSERVATION, observation, 0));
            convo.append("Action: ").append(toolName).append(" ").append(params.toJSONString()).append("\n");
            convo.append("Observation: ").append(observation).append("\n");
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

    /**
     * 鲁棒JSON解析:模型可能输出```json ... ```代码块或前后带杂文本
     */
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
