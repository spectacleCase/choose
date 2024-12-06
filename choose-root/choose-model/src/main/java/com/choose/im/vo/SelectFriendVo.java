package com.choose.im.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/6 下午8:52
 */
@Data
public class SelectFriendVo {

    private String id;

    private String avatar;

    private String nickname;

    private String gender;

    private String phone;

    private String description;

    private int status;
}
