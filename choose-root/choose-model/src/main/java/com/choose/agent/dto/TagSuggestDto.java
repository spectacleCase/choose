package com.choose.agent.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 美食社区智能标签提取入参 (论文 5.2)。
 */
@Data
public class TagSuggestDto {

    /** 用户发布的内容 (菜名 + 简介合并即可) */
    @NotBlank
    private String content;
}
