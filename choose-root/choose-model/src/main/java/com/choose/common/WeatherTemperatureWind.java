package com.choose.common;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/11 下午8:15
 */
@Data
public class WeatherTemperatureWind {

    /**
     * 天气
     */
   private String weather;

    /**
     * 温度
     */
   private String temperature;

    /**
     * 风力
     */
   private String taste;

    /**
     * 口味标签
     */
   private String tag;
}
