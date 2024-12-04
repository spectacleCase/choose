package com.choose.im.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024/11/23 上午11:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_chat")
public class ChatMessage extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 信息类型（0:文本，1:图片）
     */
    private Integer type;

    /**
     * 发送方
     */
    private Long sender;

    /**
     * 接受方
     */
    private Long receiver;

    /**
     * 信息内容
     */
    private String content;
}

