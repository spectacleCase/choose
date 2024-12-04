package com.choose.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 桌角的眼镜
 */
@Slf4j
public class JWTUtils {
    // 签名密钥
    private static final String SECRET = "!CHOOSE$";

    private static final Integer TOKEN_TIMEOUT = 7;


    /**
     * 生成token
     *
     * @param payload token携带的信息
     * @return token字符串
     */
    public static String getToken(Map<String, String> payload) {
        // 指定token过期时间为7天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, TOKEN_TIMEOUT);

        JWTCreator.Builder builder = JWT.create();
        // 构建payload
        payload.forEach(builder::withClaim);
        // 指定过期时间和签名算法
        return builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SECRET));
    }


    /**
     * 解析token
     *
     * @param token token字符串
     * @return 解析后的token
     */
    public static DecodedJWT decode(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        log.info("token:{}", token);
        return decodedJWT;
    }


    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", "1");
        System.out.println(getToken(map));
    }

}
