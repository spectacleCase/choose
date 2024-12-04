package com.choose.ranking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 下午1:01
 */
@Data
public class RankingDto {

    /**
     * 获取id
     */
    @NotNull
    private Long id;

    /**
     * 页数
     */
    @NotNull
    private Integer page;
}
