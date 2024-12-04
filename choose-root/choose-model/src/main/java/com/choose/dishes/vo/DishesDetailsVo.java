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
 * @since 2024/10/24 下午12:38
 */
@Data
public class DishesDetailsVo {

    /**
     * id
     */
    private String id;

    /**
     * 菜品名称
     */
    private String dishesName;

    /**
     * 图片
     */
    private String image;

    /**
     * 标签列表
     */
    private List<String> tags;

    // todo 评论
}
