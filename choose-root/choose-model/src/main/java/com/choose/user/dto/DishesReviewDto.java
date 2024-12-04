package com.choose.user.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 下午8:20
 */
@Data
public class DishesReviewDto {

    @NotNull(message = "菜品为空")
    private Long dishesId;

    @NotNull(message = "需要页数")
    private Integer page;
}
