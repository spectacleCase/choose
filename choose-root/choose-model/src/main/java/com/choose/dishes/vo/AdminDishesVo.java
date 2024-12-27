package com.choose.dishes.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/27 01:19
 */
@Data
public class AdminDishesVo {

    private String id;

    private String foodName;

    private String shopName;

    private Date submitTime;

    private String location;

    private String longitude;

    private String latitude;

    private List<String> tags;

    private List<String> imageUrl;
}
