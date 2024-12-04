package com.choose.comment.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/27 下午10:15
 */
@Data
public class ShopCommentDetailVo {

    private String id;

    private String userId;

    private String avatar;

    private String userName;

    private String createTime;

    private String content;

    private String images;

    private String replyToName;

    private List<ShopCommentDetailVo> subComments;
}

