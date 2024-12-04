package com.choose.dishes.pojos;

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
 * @since 2024/6/3 下午1:29
 */

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_dishes")
public class Dishes extends BasePo {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 菜品名称
     */
    private String dishesName;

    /**
     * 图片url
     */
    private String image;

    /**
     * 所属栏目id
     */
    private Long columnId;

    /**
     * 所属店铺id
     */
    private Long shop;

    /**
     * 评分
     */
    private Double mark;

    /**
     * 审核状态（-1审核失败，0未审核，1审核通过）
     */
    private Integer isAudit;

}
