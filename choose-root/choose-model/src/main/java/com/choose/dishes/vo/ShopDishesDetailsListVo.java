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
 * @since 2024/10/23 下午4:43
 */
@Data
public class ShopDishesDetailsListVo {
    /**
     * id
     */
    private String id;

    /**
     * 菜品名称
     */
    private String dishesName;

    /**
     * 菜品图片
     */
    private String dishesImage;

    /**
     * 菜品标签
     */
    private List<String> dishesTags;
}
