package com.choose.enums;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/30 下午9:40
 */
public enum ChatEnum {

    fail_AUDIT(0, "文本"),
    UN_AUDIT(1, "图片"),
    ;

    final int code;
    final String msg;

    ChatEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static int getCode(String msg) {
        ChatEnum[] r = ChatEnum.values();
        for (ChatEnum chatEnum : r) {
            if (Objects.equals(chatEnum.msg, msg)) {
                return chatEnum.code;
            }
        }
        return -1;
    }

    public static String getMsg(int code) {
        ChatEnum[] r = ChatEnum.values();
        for (ChatEnum chatEnum : r) {
            if (chatEnum.code == code) {
                return chatEnum.msg;
            }
        }
        return null;
    }
}
