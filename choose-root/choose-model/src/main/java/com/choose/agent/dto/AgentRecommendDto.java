package com.choose.agent.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Agent协作推荐请求。
 */
@Data
public class AgentRecommendDto {

    /** 用户自然语言查询 */
    @NotBlank
    private String query;

    /** 用户当前坐标 lng,lat,可选 */
    private String location;

    /** 期望返回数量,默认5 */
    private Integer num;

    /** 是否走异步营养评估路径 (论文 3.2),默认 false */
    private Boolean asyncNutrition;
}
