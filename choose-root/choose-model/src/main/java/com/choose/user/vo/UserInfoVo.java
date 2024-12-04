package com.choose.user.vo;

import com.choose.user.pojos.UserInfo;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 下午9:03
 */
@Data
public class UserInfoVo {

    private String token;

    private Date tokenTimeout;

    private UserInfo userInfo;
}
