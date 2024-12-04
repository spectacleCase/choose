package com.choose.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 下午5:59
 */
@Data
public class ReviewDto {

    @NotNull(message = "菜品为空")
    private Long dishesId;

    @NotBlank(message = "评论为空")
    private String review;
}
