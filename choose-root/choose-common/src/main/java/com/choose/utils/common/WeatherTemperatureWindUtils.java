package com.choose.utils.common;

import com.choose.common.WeatherTemperatureWind;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/11 下午8:17
 */
@Slf4j
public class WeatherTemperatureWindUtils {

    public static Map<String, WeatherTemperatureWind> commonWeatherTemperatureWind = new HashMap<>();


    public static void init() {
        log.info("start init");
        commonWeatherTemperatureWind
                = readWeatherData("choose-root/choose-common/src/main/java/com/choose/file/天气-口味-对应标签表.txt");
        log.info("end init");
    }


    public static Map<String, WeatherTemperatureWind> getWeatherTemperatureWind(String weather, int temperature, int taste) {
        if (Objects.isNull(commonWeatherTemperatureWind) || commonWeatherTemperatureWind.isEmpty()) {
            // throw new CustomException(AppHttpCodeEnum.MAP_INIT_LOSE);
        }

        temperature = temperature - 2;
        taste = taste - 2;
        HashMap<String, WeatherTemperatureWind> stringWeatherTagHashMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String key = weather + "-" + (temperature + i) + "°C" + "-" + (taste + j);
                WeatherTemperatureWind weatherTag = commonWeatherTemperatureWind.get(key);
                if (Objects.isNull(weatherTag)) {
                    continue;
                }
                stringWeatherTagHashMap.put(key, weatherTag);
            }
        }
        return stringWeatherTagHashMap;
    }


    /**
     * 读取文本的内容
     */
    public static Map<String, WeatherTemperatureWind> readWeatherData(String filePath) {
        Map<String, WeatherTemperatureWind> weatherMap = new HashMap<>();
        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    WeatherTemperatureWind weatherTag = new WeatherTemperatureWind();
                    weatherTag.setWeather(parts[0]);
                    weatherTag.setTemperature(parts[1]);
                    weatherTag.setTaste(parts[2]);
                    weatherTag.setTag(parts[3]);

                    String key = weatherTag.getWeather() + "-" + weatherTag.getTemperature() + "-" + weatherTag.getTaste();
                    weatherMap.put(key, weatherTag);
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }

        return weatherMap;
    }
}
