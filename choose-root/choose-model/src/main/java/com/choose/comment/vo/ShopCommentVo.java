package com.choose.comment.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 桌角的眼镜
 */
@Data
public class ShopCommentVo {

    /**
     * id
     */
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 店铺id
     */
    private String topId;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 评论文本
     */
    private String content;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 用户头像（冗余字段）
     */
    private String userAvatar;

    /**
     * 用户名（冗余字段）
     */
    private String userName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子评论列表
     */
    private List<ShopCommentVo> children;
}