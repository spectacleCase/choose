package com.choose.service.auto.impl;

import cn.hutool.http.HttpUtil;
import com.choose.josn.JsonUtil;
import com.choose.user.pojos.SessionKeyAndOpenId;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 下午9:24
 */
@Slf4j
public class WeChatService{

    /**
     * 调用微信接口响应用户数据
     */
    public static SessionKeyAndOpenId getWeChatAccessTokenResponse(String appid, String secret, String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
        String formattedUrl = String.format(url, appid, secret, code);

        try {
            String res = HttpUtil.get(formattedUrl);
            SessionKeyAndOpenId sessionKeyAndOpenId = JsonUtil.toInstance(res, SessionKeyAndOpenId.class);
            if(!Objects.isNull(sessionKeyAndOpenId.getErrmsg())){
                log.error("not openid");
                // throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
                throw new RuntimeException();
            }
            log.info("login user res: {}", res);
            return sessionKeyAndOpenId;
        } catch (Exception e) {
            log.error("Error occurred while calling WeChat API", e);
            // throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
