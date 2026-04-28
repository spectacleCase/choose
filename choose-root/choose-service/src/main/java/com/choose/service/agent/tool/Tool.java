package com.choose.service.agent.tool;

import com.alibaba.fastjson.JSONObject;
import com.choose.service.agent.core.AgentContext;

/**
 * 工具调用统一接口。每个工具自描述名称、用途、参数schema(自然语言),供Agent在ReAct推理中决定调用。
 */
public interface Tool {

    /** 工具名,Agent的Action.name必须与此匹配 */
    String name();

    /** 工具用途,会被拼到System Prompt里 */
    String description();

    /** 参数说明,自由文本格式,会被拼到System Prompt里 */
    String parameterSchema();

    /**
     * 执行工具
     * @param params Agent输出的Action.params
     * @param ctx 共享上下文,允许工具读取/写入
     * @return Observation文本(返回给模型作为下一轮上下文)
     */
    String execute(JSONObject params, AgentContext ctx);
}
