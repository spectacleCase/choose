package com.choose.enums;

import lombok.Getter;

/**
 * 消息类型
 * 评论类型
 *
 * @author 桌角的眼镜
 */

@Getter
public enum CommentEnum {
    COMMENT(1, "评论"),
    SYSTEM(2, "系统"),

    READ(1, "已读"),
    UNREAD(0, "未读");

    final int code;
    final String msg;

    CommentEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
