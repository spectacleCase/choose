package com.choose.comment.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/27 下午8:48
 */
@Data
public class AllShopCommentVo {

    private String id;

    private String avatar;

    private String userName;

    private String createTime;

    private String content;

    private String images;

    private Integer number;

    private ShopUserCommentVo subComment;
}
