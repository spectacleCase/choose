package com.choose.user.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 下午9:04
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {

    /**
     * id
     */
    private String id;
    /**
     * 小程序openid
     */
    private String openid;
    /**
     * 头像url
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别(0 男,1 女,2不确定)
     */
    private String  gender;
    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 个性化签名
     */
    private String description;

}
