package com.choose.dishes.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/3 下午1:25
 */
@Data
public class ShopListVo {
    /**
     * 主键ID
     */
    private String id;

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
}
