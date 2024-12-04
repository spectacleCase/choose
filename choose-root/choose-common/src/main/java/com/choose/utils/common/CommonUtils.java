package com.choose.utils.common;

import com.choose.common.WeatherVo;

import java.util.HashMap;


/**
 * @author 桌角的眼镜
 */
public class CommonUtils extends com.choose.common.CommonUtils {


    public static WeatherVo newGetWeather() {
        HashMap<String, String> weather = getWeather();
        WeatherVo weatherVo = new WeatherVo();
        if (weather != null) {
            weatherVo.setWeather(weather.get("weather"));
            weatherVo.setTemperature(weather.get("temperature"));
            weatherVo.setWindpower(weather.get("windpower"));
            return weatherVo;
        }

        return null;
    }
}
