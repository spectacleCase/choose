package com.choose.service.comment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.comment.pojos.CommentNotifications;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/24 下午6:00
 */
public interface CommentNotificationsService extends IService<CommentNotifications> {

    /**
     * 获取全部未读消息
     */
    Map<String,Long> getIsReadNum();

    /**
     * 阅读评论
     */
    void read();
}
