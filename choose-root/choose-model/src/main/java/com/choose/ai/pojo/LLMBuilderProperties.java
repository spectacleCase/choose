package com.choose.ai.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LLMBuilderProperties {
    /**
     *
     */
    private Double temperature;

    /**
     * 获取采样温度，如果温度不合法则获取默认温度
     */
    public Double getTemperatureWithDefault(double defaultTemperature) {
        if (defaultTemperature < 0 || defaultTemperature > 1) {
            // throw new Exception();
            // todo 抛异常
        }
        if (Objects.isNull(temperature)) {
            return defaultTemperature;
        }
        if (temperature < 0 || temperature > 1) {
            return defaultTemperature;
        }
        return temperature;
    }

}