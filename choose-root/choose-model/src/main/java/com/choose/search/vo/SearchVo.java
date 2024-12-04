package com.choose.search.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/20 下午3:37
 */
@Data
public class SearchVo {
    /**
     * 店铺id
     */
    private String shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 评分
     */
    private Double rating;


    // private Integer reviewCount;
    /**
     * 店铺图片
     */
    private String shopImage;

    /**
     * 地址
     */
    private String address;

    /**
     * 菜品列表
     */
    private List<SearchDishesVo> dishesList;

}
