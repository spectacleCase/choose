package com.choose.service.recommend.pojo;

import lombok.Data;

/**
 * @author 桌角的眼镜
 */
@Data
public class Context {

    // 天气
    String weather;
    // 温度
    String temperature;
    // 距离
    String distance;
    // 风速
    String windSpeed;

    public Context(String weather, String temperature, String distance, String windSpeed) {
        this.weather = weather;
        this.temperature = temperature;
        this.distance = distance;
        this.windSpeed = windSpeed;
    }



}