package com.choose.config;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.choose.admin.user.dto.User;
import com.choose.constant.CommonConstants;
import com.choose.redis.utils.RedisUtils;
import com.choose.user.pojos.UserInfo;
import com.choose.josn.JsonUtil;
import com.choose.jwt.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


/**
 * @author 桌角的眼镜
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisUtils redisUtils;

    //Controller方法处理之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        //获取到请求头的jwt字符串
        String token = request.getHeader("Token");
        String adminToken = request.getHeader("AdminToken");
        //验证token
        try {
            if (Objects.isNull(token)  && Objects.isNull(adminToken)) {
                return false;
            }
            if(Objects.nonNull(token) && !token.isEmpty()) {
                DecodedJWT decode = JWTUtils.decode(token);
                Claim id = decode.getClaim("userId");
                if (id.isNull()) {
                    return false;
                }
                Object o = redisUtils.get(CommonConstants.RedisKeyPrefix.USER_TOKEN_CACHE_KEY + id.asString());
                if (!Objects.isNull(o)) {
                    if (o.equals(token)) {
                        Object userInfoJson = redisUtils.get(CommonConstants.RedisKeyPrefix.USER_INFO_TOKEN_CACHE_KEY + id.asString());
                        UserInfo userInfo = JsonUtil.toInstance((String) userInfoJson, UserInfo.class);
                        userInfo.setId(id.asString());
                        UserLocalThread.set(userInfo);
                        //放行请求
                        response.setStatus(200);
                        return true;
                    }
                }
                response.setStatus(401);
                return false;
            }
            if(Objects.nonNull(adminToken) && !adminToken.isEmpty()) {
                DecodedJWT decode = JWTUtils.decode(adminToken);
                Claim id = decode.getClaim("adminId");
                if (id.isNull()) {
                    return false;
                }
                Object o = redisUtils.get(CommonConstants.RedisKeyPrefix.ADMIN_TOKEN_CACHE_KEY + id.asString());
                if (!Objects.isNull(o)) {
                    if (o.equals(adminToken)) {
                        Object userInfoJson = redisUtils.get(CommonConstants.RedisKeyPrefix.ADMIN_INFO_TOKEN_CACHE_KEY + id.asString());
                        User userInfo = JsonUtil.toInstance((String) userInfoJson, User.class);
                        userInfo.setId(Long.valueOf(id.asString()));
                        UserLocalThread.set(userInfo);
                        //放行请求
                        response.setStatus(200);
                        return true;
                    }
                }
                response.setStatus(401);
                return false;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            //相应码401
            response.setStatus(401);
            //拦截请求
            return false;
        }
        return false;
    }

    //该方法将在整个请求完成之后执行
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        //清空UserLocalTread中的信息 防止内存泄漏
        UserLocalThread.remove();
    }

}