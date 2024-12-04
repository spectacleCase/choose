package com.choose.comment.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/25 下午4:12
 */
@Data
public class UserCommentVo {

    /**
     * 评论id
     */
    private String commentId;

    /**
     * 回复用户名
     */
    private String userName;

    /**
     * 回复用户头像
     */
    private String userAvatar;

    /**
     * 评论文本
     */
    private String content;

    /**
     * 评论时间
     */
    private String createTimeText;

    // /**
    //  * 店铺id
    //  */
    // private String shopId;
    //
    // /**
    //  * 店铺名称
    //  */
    // private String shopName;
    //
    // /**
    //  * 店铺图片
    //  */
    // private String shopImage;

    /**
     * 顶级id
     */
    private String topId;
}
