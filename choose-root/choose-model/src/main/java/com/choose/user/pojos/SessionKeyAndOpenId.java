package com.choose.user.pojos;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 下午10:03
 */
@Data
public class SessionKeyAndOpenId {
    private String errcode;
    private String errmsg;
    private String rid;
    private String sessionKey;
    private String openId;
}
