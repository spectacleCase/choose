package com.choose.config;


import com.choose.admin.user.dto.User;
import com.choose.user.pojos.UserInfo;


/**
 * <p>
 * 用户线程信息
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午1:15
 */
public class UserLocalThread {
    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<User> ADMIN_THREAD_LOCAL = new ThreadLocal<>();

    public static UserInfo getUser() {
        return USER_THREAD_LOCAL.get();
    }
    public static User getAdmin() {
        return ADMIN_THREAD_LOCAL.get();
    }

    public static void set(UserInfo userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    public static void set(User user) {
        ADMIN_THREAD_LOCAL.set(user);
    }

    public static void remove() {
        USER_THREAD_LOCAL.remove();
    }

    public static void removeAdmin() {
        ADMIN_THREAD_LOCAL.remove();
    }
}
