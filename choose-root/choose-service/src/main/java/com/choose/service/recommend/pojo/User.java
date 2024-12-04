package com.choose.service.recommend.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 桌角的眼镜
 */
@Data
public class User {
    // 用户id
    String id;

    // 用户对不同食物类别的偏好
    Map<String, Double> preferences;

    public User(String id) {
        this.id = id;
        this.preferences = new HashMap<>();
    }

    public void setPreference(String foodType, double preference) {
        preferences.put(foodType, preference);
    }

    public double getPreference(String foodType) {
        return preferences.getOrDefault(foodType, 0.0);
    }
}