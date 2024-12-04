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
 * @since 2024/10/24 下午5:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_shop_comment")
public class ShopComment  extends BasePo {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 顶级id
     */
    private Long topId;

    /**
     * 父级id
     */
    private Long parentId;

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

}
