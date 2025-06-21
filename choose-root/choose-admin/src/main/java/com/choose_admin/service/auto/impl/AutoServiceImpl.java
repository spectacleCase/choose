package com.choose_admin.service.auto.impl;

import com.alibaba.fastjson2.JSON;
import com.choose.admin.user.dto.AdminsProperties;
import com.choose.admin.user.dto.User;
import com.choose.admin.user.dto.UserInfoVo;
import com.choose.annotation.SysLog;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.jwt.JWTUtils;
import com.choose.redis.utils.RedisUtils;
import com.choose_admin.service.auto.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/26 下午12:37
 */
@Service
public class AutoServiceImpl implements AutoService {

    private final AdminsProperties admins;

    @Resource
    private RedisUtils redisUtils;

    @Value("${auth.redis-token.timeout}")
    private String tokenTimeout;


    @Autowired
    public AutoServiceImpl(AdminsProperties adminsProperties) {
        this.admins = adminsProperties;
    }

    /**
     * 管理员登录
     */
    @Override
    public UserInfoVo login(User user) {
        Map<String, User> users = admins.getUsers();
        User admin = users.get(user.getUsername());
        if (Objects.nonNull(admin)) {
            if (Objects.equals(user.getPassword(), admin.getPassword())) {
                UserLocalThread.set(admin);
                String tokenValue = buildToken(admin);
                UserInfoVo userInfoVo = new UserInfoVo();
                userInfoVo.setToken(tokenValue);
                userInfoVo.setId(admin.getId());
                UserLocalThread.set(user);
                return userInfoVo;
            }
        }
        throw new RuntimeException();
    }



    public String buildToken(User user) {
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("adminId", user.getId().toString());
        String token = JWTUtils.getToken(tokenMap);

        // 构建Redis key
        String tokenKey = CommonConstants.RedisKeyPrefix.ADMIN_TOKEN_CACHE_KEY + user.getId();
        String userInfoKey = CommonConstants.RedisKeyPrefix.ADMIN_INFO_TOKEN_CACHE_KEY + user.getId();
        // User userInfo = new User();
        // BeanUtils.copyProperties(user, userInfo);
        String userInfoJson = JSON.toJSONString(user);
        if (userInfoJson.isEmpty()) {
            return null;
        }
        // 存储 到 Redis
        redisUtils.set(tokenKey, token, Long.parseLong(tokenTimeout));
        redisUtils.set(userInfoKey, userInfoJson, Long.parseLong(tokenTimeout));
        return token;
    }

    /**
     * 登录退出
     */
    @Override
    public void logout() {
        User admin = UserLocalThread.getAdmin();
        if (Objects.nonNull(admin)) {
            redisUtils.del(CommonConstants.RedisKeyPrefix.ADMIN_TOKEN_CACHE_KEY + admin.getId());
            redisUtils.del(CommonConstants.RedisKeyPrefix.ADMIN_INFO_TOKEN_CACHE_KEY + admin.getId());
            return;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }
}
