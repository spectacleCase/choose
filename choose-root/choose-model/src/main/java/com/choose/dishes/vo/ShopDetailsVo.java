package com.choose.dishes.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/23 下午4:42
 */
@Data
public class ShopDetailsVo {

    /**
     * id
     */
    private String id;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺图片
     */
    private String image;

    /**
     * 店铺评分
     */
    private String mark;

    /**
     * 地址
     */
    private String coordinate;

    /**
     * 菜品列表
     */
    private List<ShopDishesDetailsListVo> dishesDetailsList;

    //todo 缺失评论列表


}
