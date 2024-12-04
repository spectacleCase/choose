package com.choose.comment.dto;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/27 下午10:09
 */
public record ShopCommentDetailDto(@NotBlank(message = "评论id不允许为空") String commentId) {
}
