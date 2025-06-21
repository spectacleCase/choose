package com.choose.comment.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/20 22:50
 */
@Data
public class CommentNotifMqVo {

    public CommentNotifMqVo() {
    }

    public CommentNotifMqVo(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 信息体
     */
    private String message;
}
