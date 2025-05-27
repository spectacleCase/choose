package com.choose.annotation;

import java.lang.annotation.*;


/**
 * 定义系统日志注解
 *
 * @author 桌角的眼镜
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String value() default "";
}