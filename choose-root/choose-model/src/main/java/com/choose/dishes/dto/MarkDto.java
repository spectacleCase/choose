package com.choose.dishes.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 下午4:48
 */
@Data
public class MarkDto {

    @NotNull(message = "参数不能为空")
    private Long dishesId;

    @Min(value = 1, message = "分数必须大于等于1")
    @Max(value = 100, message = "分数必须小于等于100")
    private Integer mark;
}
