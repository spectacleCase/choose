package com.choose.config;

import com.choose.utils.common.WeatherTemperatureWindUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 桌角的眼镜
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // 允许所有来源
        corsConfig.addAllowedOrigin("*");
        // 允许所有HTTP方法
        corsConfig.addAllowedMethod("*");
        // 允许所有HTTP标头
        corsConfig.addAllowedHeader("*");
        // 项目使用了 sa-token,并且是使用 token 前后端分离的方式，
        // 并不是使用 cookies传递用户token，所以要设置为false 不允许携带身份信息（如Cookies）
        corsConfig.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public void commonMap() {
        WeatherTemperatureWindUtils.init();
    }
}
