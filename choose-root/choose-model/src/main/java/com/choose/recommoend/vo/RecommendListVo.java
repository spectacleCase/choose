package com.choose.recommoend.vo;

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
 * @since 2024/6/3 下午1:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecommendListVo extends BasePo {

    /**
     * 主键id
     */
    private String id;

    /**
     * 店铺id
     */
    private String shopId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 菜品id
     */
    private String dishesId;

    /**
     * 菜品图片
     */
    private String image;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 菜名
     */
    private String dishesName;

    /**
     * 路程
     */
    private String distance;

    /**
     * ai描述
     */
    private String description;


}
