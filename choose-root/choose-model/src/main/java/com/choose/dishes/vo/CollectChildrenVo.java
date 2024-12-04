package com.choose.dishes.vo;

import lombok.Data;

/**
 * @author chen
 */
@Data
public class CollectChildrenVo {
    private String id;
    private String dishesName;
    private String dishesImage;
    private String coordinate;
    private String dishId;
    private Boolean isCollect;
}
