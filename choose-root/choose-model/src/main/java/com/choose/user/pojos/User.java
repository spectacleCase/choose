package com.choose.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 桌角的眼镜
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_user")
public class User extends BasePo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
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

    private String sessionKey;

    /**
     * 用户状态
     */
    private Integer status;
    /**
     * 最近登录时间
     */
    private Date lastLogin;
    /**
     * 封禁开始时间
     */
    private Date banStartTime;
    /**
     * 封禁结束时间
     */
    private Date banEndTime;

    /**
     * 个性化签名
     */
    private String description;
}
