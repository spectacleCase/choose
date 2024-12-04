package com.choose.service.recommend.pojo;

import lombok.Data;

/**
 * @author 桌角的眼镜
 */
@Data
public class Food {
    // 食物id
    String id;
    // 食物类别
    String type;

    public Food(String id, String type) {
        this.id = id;
        this.type = type;
    }
}