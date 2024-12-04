package com.choose.user.pojos;

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
 * @since 2024/6/7 下午5:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_review")
public class Review extends BasePo {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 菜品id
     */
    private Long dishesId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 评论文本内容
     */
    private String review;
}
