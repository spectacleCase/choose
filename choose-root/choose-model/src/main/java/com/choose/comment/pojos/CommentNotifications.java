package com.choose.comment.pojos;

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
 * @since 2024/10/24 下午5:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_comment_notifications")
public class CommentNotifications extends BasePo {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 触发用户id
     */
    private Long senderId;

    /**
     * 类型（1评论 2系统）
     */
    private Integer type;

    /**
     * 消息
     */
    private String message;

    /**
     * 是否已读(1读 0未读)
     */
    private Integer isRead;

    /**
     * 触发者名字
     */
    private String senderName;

    /**
     * 触发者头像
     */
    private String senderAvatar;


}
