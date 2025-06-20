package com.choose.service.comment.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.annotation.SysLog;
import com.choose.comment.dto.AddShopCommentDto;
import com.choose.comment.dto.ShopCommentDetailDto;
import com.choose.comment.dto.ShopCommentDto;
import com.choose.comment.pojos.CommentNotifications;
import com.choose.comment.pojos.ShopComment;
import com.choose.comment.vo.*;
import com.choose.common.dto.CommentPageDto;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.constant.FileConstant;
import com.choose.dishes.pojos.Shops;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.enums.CommentEnum;
import com.choose.exception.CustomException;
import com.choose.mapper.CommentNotificationsMapper;
import com.choose.mapper.ShopCommentMapper;
import com.choose.mapper.ShopsMapper;
import com.choose.mapper.UserMapper;
import com.choose.service.comment.ShopCommentService;
import com.choose.service.im.impl.NotificationWebSocketHandlerServer;
import com.choose.user.pojos.UserInfo;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.junit.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class ShopCommentServiceImpl extends ServiceImpl<ShopCommentMapper, ShopComment> implements ShopCommentService {

    @Resource
    private ShopCommentMapper shopCommentMapper;

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private CommentNotificationsMapper commentNotificationsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private NotificationWebSocketHandlerServer notificationWebSocketHandlerServer;


    /**
     * 添加店铺评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addShopComment(AddShopCommentDto dto) {
        boolean result = false;
        if (dto.topId().isEmpty() || Objects.nonNull(shopCommentMapper.selectById(dto.topId()))) {
            UserInfo user = UserLocalThread.getUser();
            ShopComment shopComment = new ShopComment();
            shopComment.setUserId(Long.valueOf(user.getId()));
            shopComment.setUserName(user.getNickname());
            shopComment.setUserAvatar(user.getAvatar());
            if(!dto.topId().isEmpty()){
                shopComment.setTopId(Long.valueOf(dto.topId()));
            }
            if (!dto.parentId().isEmpty()) {
                shopComment.setParentId(Long.valueOf(dto.parentId()));
            }
            if (!dto.imageUrl().isEmpty()) {
                shopComment.setImageUrl(dto.imageUrl());
            }
            String content = dto.content();
            if(SensitiveWordBs.newInstance().contains(content)) {
                content = SensitiveWordBs.newInstance().replace(content);
            }
            shopComment.setContent(content);
            this.save(shopComment);

            // 推送通知
            CommentNotifications commentNotifications = new CommentNotifications();
            commentNotifications.setCommentId(shopComment.getId());
            commentNotifications.setUserId(Long.valueOf(user.getId()));
            if (!dto.parentId().isEmpty()) {
                commentNotifications.setSenderId(Long.valueOf(dto.senderId()));
                commentNotifications.setSenderName(dto.senderName());
                commentNotifications.setSenderAvatar(dto.senderAvatar());
                result = true;

            }

            commentNotifications.setType(CommentEnum.COMMENT.getCode());
            commentNotifications.setMessage(content);
            commentNotifications.setIsRead(CommentEnum.UNREAD.getCode());
            commentNotificationsMapper.insert(commentNotifications);
            String message = "{\"messageType\":\"notify\",\"content\":\"" + content + "\"}";
            if (result) {
                notificationWebSocketHandlerServer.sendComment(Long.valueOf(dto.senderId()), message);
            }

            return;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);

    }

    /**
     * 获取发送给用户的消息
     */
    @Override
    public List<UserCommentVo> getUserComment(CommentPageDto dto) {
        int offset = (dto.getPage() - 1) * CommonConstants.Common.PANE_SIZE;
        List<UserCommentVo> userCommentVos = commentNotificationsMapper
                .selectUserCommentVoList(Long.valueOf(UserLocalThread.getUser().getId()), offset, CommonConstants.Common.PANE_SIZE);
        userCommentVos.forEach(com -> {
            if(Objects.isNull(com.getTopId())) {
                com.setTopId(com.getCommentId());
            }
            com.setUserAvatar(FileConstant.COS_HOST + com.getUserAvatar());
        });
        return userCommentVos;
    }


    /**
     * 获取店铺的全部评论
     */
    @Override
    @SysLog("获取帖子全部评论")
    public List<ShopCommentVo> getShopCommentList(ShopCommentDto dto) {
        Shops shops = shopsMapper.selectById(dto.shopId());
        if (Objects.nonNull(shops)) {
            int offset = (dto.page() - 1) * 5;
            List<ShopCommentVo> shopCommentVos = shopCommentMapper.selectShopCommentList(Long.valueOf(dto.shopId()), offset, 5);
            addUrl(shopCommentVos);
            return shopCommentVos;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }

    private void addUrl(List<ShopCommentVo> vo) {
        for (ShopCommentVo shopCommentVo : vo) {
            if (!shopCommentVo.getChildren().isEmpty()) {
                addUrl(shopCommentVo.getChildren());
            }
            shopCommentVo.setUserAvatar(FileConstant.COS_HOST + shopCommentVo.getUserAvatar());
        }
    }

    /**
     * 获取店铺的全部评论
     */
    @Override
    @SysLog("获取帖子全部评论")
    public List<AllShopCommentVo> getShopAllCommentList(ShopCommentDto dto) {
        Shops shops = shopsMapper.selectById(dto.shopId());
        if (Objects.nonNull(shops)) {
            int offset = (dto.page() - 1) * 5;
            List<AllShopCommentVo> allShopCommentVos = shopCommentMapper.selectAllComments(Long.valueOf(dto.shopId()), offset, 5);
            allShopCommentVos.forEach(item -> {
                item.setAvatar(FileConstant.COS_HOST + item.getAvatar());
                if (Objects.nonNull(item.getSubComment())) {
                    item.getSubComment().setAvatar(FileConstant.COS_HOST + item.getSubComment().getAvatar());
                }
                if (Objects.nonNull(item.getImages()) && !item.getImages().isEmpty()) {
                    item.setImages(extracted(item.getImages()));
                }
            });
            return allShopCommentVos;
        }
        throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
    }

    @Override
    @SysLog("获取帖子全部评论")
    public ShopCommentDetailVo getShpCommentDetails(ShopCommentDetailDto dto) {
        ShopCommentDetailVo shopCommentDetailVo = shopCommentMapper.selectCommentDetailById(Long.valueOf(dto.commentId()));
        addUrl(shopCommentDetailVo);
        List<ShopCommentDetailVo> subComments = shopCommentDetailVo.getSubComments();
        for (ShopCommentDetailVo subComment : subComments) {
            List<ShopCommentDetailVo> shopCommentDetailVos = new ArrayList<>();
            for (ShopCommentDetailVo comment : subComment.getSubComments()) {
                if (!comment.getSubComments().isEmpty()) {
                    extracted(shopCommentDetailVos, comment.getSubComments());
                    comment.setSubComments(shopCommentDetailVos);
                }
                shopCommentDetailVos = new ArrayList<>();
            }
        }
        return shopCommentDetailVo;
    }

    private void extracted(List<ShopCommentDetailVo> li, List<ShopCommentDetailVo> vo) {
        for (ShopCommentDetailVo shopCommentDetailVo : vo) {
            if (!shopCommentDetailVo.getSubComments().isEmpty()) {
                extracted(li, shopCommentDetailVo.getSubComments());
            }
            System.out.println(shopCommentDetailVo);
            li.add(shopCommentDetailVo);
            shopCommentDetailVo.setSubComments(new ArrayList<>());
        }
    }

    private void addUrl(ShopCommentDetailVo vo) {
        vo.setAvatar(FileConstant.COS_HOST + vo.getAvatar());
        if (Objects.nonNull(vo.getImages()) && !vo.getImages().isEmpty()) {
            vo.setImages(extracted(vo.getImages()));
        }
        if (!vo.getSubComments().isEmpty()) {
            vo.getSubComments().forEach(this::addUrl);
        }
    }

    private String extracted(String img) {
        StringBuilder images = new StringBuilder(img);
        String[] split = images.toString().split(",");
        images = new StringBuilder();
        for (String s : split) {
            images.append(FileConstant.COS_HOST).append(s).append(",");
        }
        return images.substring(0, images.length() - 1);
    }

    /**
     * 获取最新的评论列表
     */
    @Override
    @SysLog("获取最新的评论列表")
    public List<LatestCommentListVo> getLatestCommentList(CommentPageDto dto, Boolean sortByNums) {
        int offset = (dto.getPage() - 1) * CommonConstants.Common.PANE_SIZE;
        List<LatestCommentListVo> latestCommentList = shopCommentMapper.getLatestCommentList(offset, CommonConstants.Common.PANE_SIZE, sortByNums);
        latestCommentList.forEach(item -> {
            item.setAvatar(FileConstant.COS_HOST + item.getAvatar());
            if (Objects.nonNull(item.getImages())) {
                item.setImages(extracted(item.getImages()));
            }
        });
        return latestCommentList;
    }
}
