package com.choose.im.pojos;

import lombok.Data;

/**
 * <p>
 * im信息模板
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/23 上午11:17
 */
@Data
public class ImMessage {
    /**
     * 信息类型
     */
    private String type;

    /**
     * 发送方
     */
    private String sender;

    /**
     * 接受方
     */
    private String receiver;

    /**
     * 信息内容
     */
    private String content;

    /**
     *  发送时间
     */
    private String createTime;
}
