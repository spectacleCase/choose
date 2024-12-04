package com.choose.comment.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/3 下午5:05
 */
@Data
public class LatestCommentListVo {

    /**
     * id
     */
    private String id;

    /**
     * 用户名
     */
    private  String name;

    /**
     * 头像
     */
    private  String avatar;

    /**
     * 时间文本
     */
    private String createTimeText;

    /**
     * 评论文本
     */
    private String content;

    /**
     * 图片url
     */
    private String images;

    /**
     * 评论数量
     */
    private Integer nums;
}
