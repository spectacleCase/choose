package com.choose.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


/**
 * @author 桌角的眼镜
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;


    @Value("#{'${system.exclude_path}'.split(',')}")
    private String[] excludePath;

    // @Value("${system.exclude_path}")
    // public void setExcludePath(String[] excludePath) {
    //     this.excludePath = excludePath;
    // }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //把自定义的拦截器注册到全局拦截器中 排除登录和注册请求
        // registry.addInterceptor(loginInterceptor).excludePathPatterns(path);
        registry.addInterceptor(loginInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }
}