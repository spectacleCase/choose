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
 * @since 2024/12/17 00:17
 */
@Data
public class AdminShopVo {

    private String id;

    private String shopName;

    private String username;

    private List<String> tagList;

    private String location;

    private String longitude;

    private String latitude;

    private List<String> imageUrls;

    private Date createTime;
}
