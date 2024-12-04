package com.choose.enums;

import lombok.Getter;

/**
 * 审核状态
 *
 * @author 桌角的眼镜
 */

@Getter
public enum AuditEnum {
    fail_AUDIT(-1, "审核失败"),
    UN_AUDIT(0, "未审核"),
    AUDIT_SUCCESS(1, "审核通过"),
    ;

    final int code;
    final String msg;

    AuditEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
