package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工具调用日志 (论文 4.3 "统一监控"原则)。
 * 与 AgentCallLog 区别: AgentCallLog 是一次 Agent 推理的整体记录,
 * ToolCallLog 是 Agent 在 ReAct 循环中实际触发的每一次工具调用的细粒度记录。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_tool_call_log")
public class ToolCallLog extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 链路ID */
    private String traceId;

    /** 调用方 Agent */
    private String agentName;

    /** 工具名 */
    private String toolName;

    /** 调用参数 JSON */
    private String params;

    /** 工具返回的 Observation 文本 */
    private String observation;

    /** 工具执行耗时 ms */
    private Long elapsedMs;

    /** SUCCESS / FAIL / DENIED */
    private String status;

    /** 失败原因 */
    private String errorMessage;
}
