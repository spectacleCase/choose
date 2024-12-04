package com.choose.user.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/2 下午10:15
 */
@Data
public class UserVo {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 性别
     */
    private String gender;

    /**
     * 个性签名
     */
    private String description;
}
