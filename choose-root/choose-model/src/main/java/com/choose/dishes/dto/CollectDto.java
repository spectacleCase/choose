package com.choose.dishes.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @author chen
 */
@Data
public class CollectDto {
    @Size(max = 12, message = "超过12个字符")
    private String name;
    private String collectChildrenId;
    private String collectId;
    private String dishesName;
    private String dishesImage;
    private String coordinate;
    private String dishId;
    private Integer pageNum;
}
