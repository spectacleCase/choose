package com.choose.im.dto;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/6 下午8:39
 */
public record FriendStatusDto(
        @NotBlank(message = "好友id不予许为空")
        String id,
        int status
) {
}
