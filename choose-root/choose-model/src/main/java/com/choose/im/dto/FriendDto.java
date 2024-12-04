package com.choose.im.dto;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/3 上午10:28
 */
public record FriendDto(
        @NotBlank(message = "好友id不予许为空")
        String friendId,
        String remark
) {
}
