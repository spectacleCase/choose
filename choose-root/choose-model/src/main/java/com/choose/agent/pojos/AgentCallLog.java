package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Agent调用日志。论文5.2/6.1 TC-11 管理端Agent监控模块的数据来源。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_agent_call_log")
public class AgentCallLog extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 链路ID,一次推荐请求贯穿多个Agent */
    private String traceId;

    /** Agent名 */
    private String agentName;

    /** 用户ID */
    private String userId;

    /** 输入文本 */
    private String input;

    /** 输出JSON */
    private String output;

    /** ReAct步骤 JSON数组 */
    private String steps;

    /** 耗时ms */
    private Long elapsedMs;

    /** SUCCESS / FAIL */
    private String status;

    /** 失败原因 */
    private String errorMessage;
}
