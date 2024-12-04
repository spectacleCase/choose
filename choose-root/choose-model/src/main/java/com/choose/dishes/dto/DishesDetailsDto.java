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
 * @since 2024/10/24 下午12:42
 */
@Data
public class DishesDetailsDto {
    /**
     * 菜品id
     */
    @NotBlank(message = "缺失菜品id")
    private String dishesId;

}
