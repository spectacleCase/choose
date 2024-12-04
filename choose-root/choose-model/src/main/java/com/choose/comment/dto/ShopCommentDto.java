package com.choose.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/26 下午4:44
 */
public record ShopCommentDto(
        @NotBlank(message = "店铺id不允许为空")
        String shopId,
        @NotNull
        Integer page,
        Integer size) {
}
