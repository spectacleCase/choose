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
 * @since 2024/10/23 下午4:36
 */
@Data
public class ShopDetailsDto {

    /**
     * 店铺id
     */
    @NotBlank(message = "缺失店铺id")
    private String shopId;
}
