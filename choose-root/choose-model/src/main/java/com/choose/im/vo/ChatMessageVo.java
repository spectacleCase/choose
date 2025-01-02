package com.choose.im.vo;

import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/8 下午9:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatMessageVo extends BasePo {

    private String id;

    /**
     * 信息类型（0:文本，1:图片）
     */
    private Integer type;

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
}
