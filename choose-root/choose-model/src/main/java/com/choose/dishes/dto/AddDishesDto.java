package com.choose.dishes.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/21 下午9:40
 */
@Data
public class AddDishesDto {

    /**
     * 菜品名称
     */
    @NotBlank(message = "菜品名称不允许为空")
    private String dishesName;

    /**
     * 图片
     */
    @NotBlank(message = "图片不允许为空")
    private String image;

    /**
     * 栏目id
     */
    @NotBlank(message = "栏目id不允许为空")
    private String columId;

    /**
     * 所属店铺id
     */
    @NotBlank(message = "所属店铺id不允许为空")
    private String shop;

    /**
     * 所属标签列表
     */
    private List<String> tagIds;

}
