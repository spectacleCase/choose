package com.choose.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件常量
 * @author 桌角的眼镜
 */
@Component
public class FileConstant {
    /**
     * COS 访问地址
     * todo 需替换配置
     */
    @Value("${system.cos_host}")
    public static String COS_HOST;


    @Value("${system.cos_host}")
    public void setCosHost(String cosHost) {
        COS_HOST = cosHost;
    }


}