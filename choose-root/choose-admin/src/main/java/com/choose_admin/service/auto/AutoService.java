package com.choose_admin.service.auto;

import com.choose.admin.user.dto.User;
import com.choose.admin.user.dto.UserInfoVo;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/26 下午12:37
 */
public interface AutoService {

    /**
     * 管理员登录
     */
    UserInfoVo login(User user);

    /**
     * 登录退出
     */
    void logout();
}
