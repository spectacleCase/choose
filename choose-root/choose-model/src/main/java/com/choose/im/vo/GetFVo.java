package com.choose.im.vo;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/7 下午5:01
 */
@Data
public class GetFVo {

    private String id;

    private String avatar;

    private String username;

    private String chat;

    private String createTime;

    private Long notReadCount;
}
