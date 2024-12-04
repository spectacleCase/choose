package com.choose.comment.dto;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/24 下午11:02
 */
public record AddShopCommentDto(
        @NotBlank(message = "内容不允许为空")
        String content,
        String senderId,
        String senderAvatar,
        String senderName,
        String parentId,
        String topId,
        String imageUrl) {
}
