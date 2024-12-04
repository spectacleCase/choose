package com.choose.recommoend.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/8 上午1:45
 */
@Data
public class RecommendVo {


    /**
     * 菜品id
     */
    private String id;

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
    private String columnId;

    /**
     * 所属店铺id
     */
    private String shopId;

    /**
     * 所属店铺名称
     */
    private String shopName;

    /**
     * 评分
     */
    private Double mark;

    /**
     * 标签
     */
    private List<String> tagName;

    /**
     * 坐标
     */
    private String coordinate;

    /**
     * ai描述
     */
    private String aiDescription;
}
