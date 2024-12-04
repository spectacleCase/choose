package com.choose.service.comment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.comment.pojos.CommentNotifications;
import com.choose.config.UserLocalThread;
import com.choose.enums.CommentEnum;
import com.choose.mapper.CommentNotificationsMapper;
import com.choose.service.comment.CommentNotificationsService;
import com.choose.user.pojos.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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
@Service
public class CommentNotificationsServiceImpl extends ServiceImpl<CommentNotificationsMapper, CommentNotifications> implements CommentNotificationsService {

    @Resource
    private CommentNotificationsMapper commentNotificationsMapper;

    /**
     * 获取全部未读消息
     */
    @Override
    public Map<String, Long> getIsReadNum() {
        UserInfo user = UserLocalThread.getUser();
        Long l = commentNotificationsMapper.selectCount(new LambdaQueryWrapper<CommentNotifications>()
                .eq(CommentNotifications::getSenderId, user.getId())
                .eq(CommentNotifications::getIsRead, CommentEnum.UNREAD.getCode()));
        Map<String, Long> objectObjectMap = new HashMap<>();
        objectObjectMap.put("num", l);
        return objectObjectMap;
    }

    /**
     * 阅读评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void read() {
        UserInfo user = UserLocalThread.getUser();
        List<CommentNotifications> commentNotifications = commentNotificationsMapper.selectList(new LambdaQueryWrapper<CommentNotifications>()
                .eq(CommentNotifications::getSenderId, user.getId())
                .eq(CommentNotifications::getIsRead, CommentEnum.UNREAD.getCode()));
        commentNotifications.forEach(commentNotification -> {
            commentNotification.setIsRead(CommentEnum.READ.getCode());
        });
        this.updateBatchById(commentNotifications,500);
    }
}
