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
 * @since 2024/6/3 下午1:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_shops")
public class Shops extends BasePo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 图片ul
     */
    private String image;

    /**
     * 坐标
     */
    private String coordinate;

    /**
     * 评分
     */
    private String mark;

    /**
     * 发布用户id
     */
    private Long userId;

    /**
     * 审核状态（-1审核失败，0未审核，1审核通过）
     */
    private Integer isAudit;
}
