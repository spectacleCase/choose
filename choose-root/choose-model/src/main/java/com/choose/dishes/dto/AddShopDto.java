package com.choose.dishes.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/21 下午9:29
 *
 */
@Data
public class AddShopDto {

    /**
     * 店铺名称
     */
    @NotBlank(message = "店铺名称不允许为空")
    private String shopName;

    /**
     * 图片地址
     */
    @NotBlank(message = "图片地址不允许为空")
    private String image;

    /**
     * 坐标
     */
    @NotBlank(message = "坐标不允许为空")
    private String coordinate;

}
