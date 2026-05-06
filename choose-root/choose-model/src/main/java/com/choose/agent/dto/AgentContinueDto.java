package com.choose.agent.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 多轮追问闭环: 用户对前一轮 clarify_question 的回复。
 */
@Data
public class AgentContinueDto {

    /** 之前一轮的链路ID */
    @NotBlank
    private String traceId;

    /** 用户的回复 */
    @NotBlank
    private String reply;
}
