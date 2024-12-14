package com.choose.common.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/8 上午9:14
 */
@Data
public class CommentPageDto {
    /**
     * 页数
     */
    @NotNull
    private Integer page;
}
