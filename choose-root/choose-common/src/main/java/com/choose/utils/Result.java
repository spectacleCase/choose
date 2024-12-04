package com.choose.utils;


import com.choose.constant.CommonConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 桌角的眼镜
 * <p>
 * 统一返回响应类
 * </p>
 */
@Data
@Slf4j
public class Result {

    private Boolean isSuccess;

    private Integer code;

    private String message;

    private Map<String, Object> data = new HashMap<>();

    public static Result ok() {
        return new Result()
                .setCode(CommonConstants.ResultCode.SUCCESS.code)
                .setMessage(CommonConstants.ResultCode.SUCCESS.message)
                .setSuccess(true);
    }

    public static Result ok(Map<String, ?> map) {
        return new Result()
                .setCode(CommonConstants.ResultCode.SUCCESS.code)
                .setMessage(CommonConstants.ResultCode.SUCCESS.message)
                .setSuccess(true)
                .putData(map);
    }

    public static Result ok(Collection<?> map) {
        return new Result()
                .setCode(CommonConstants.ResultCode.SUCCESS.code)
                .setMessage(CommonConstants.ResultCode.SUCCESS.message)
                .setSuccess(true)
                .putData(map);
    }

    public static Result ok(Object object) {
        return new Result()
                .setCode(CommonConstants.ResultCode.SUCCESS.code)
                .setMessage(CommonConstants.ResultCode.SUCCESS.message)
                .setSuccess(true)
                .putData(object);
    }

    public static Result error() {
        return new Result()
                .setCode(CommonConstants.ResultCode.ERROR.code)
                .setMessage(CommonConstants.ResultCode.ERROR.message)
                .setSuccess(false);
    }

    public static Result error(String message) {
        return new Result()
                .setCode(CommonConstants.ResultCode.ERROR.code)
                .setMessage(message)
                .setSuccess(false);
    }

    public static Result error(Integer code, String message) {
        return new Result()
                .setCode(code)
                .setMessage(message)
                .setSuccess(false);
    }


    public static Result forward() {
        return new Result().setCode(CommonConstants.ResultCode.FORWARD.code).setMessage(CommonConstants.ResultCode.FORWARD.message).setSuccess(true);
    }

    public static Result flush() {
        return new Result().setCode(CommonConstants.ResultCode.FLUSH.code).setMessage(CommonConstants.ResultCode.FLUSH.message).setSuccess(true);
    }

    public static Result refuse() {
        return new Result().setCode(CommonConstants.ResultCode.REFUSE.code).setMessage(CommonConstants.ResultCode.REFUSE.message).setSuccess(false);
    }

    private static Map<String, Object> convertObjectToMap(Object obj, @Nullable List<Class<?>> classList) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // 使私有属性可访问，JDK 17 需要配置JVM参数
            try {
                if (classList != null && matchesClassList(field.getType(), classList)) {
                    map.put(field.getName(), null);
                } else {
                    map.put(field.getName(), field.get(obj));
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }
        return map;
    }

    private static boolean matchesClassList(Class<?> fieldType, List<Class<?>> classList) {
        for (Class<?> cls : classList) {
            if (cls.isAssignableFrom(fieldType)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, Object> convertObjectToMap(Object obj) {
        return convertObjectToMap(obj, null);
    }

    public Result setData(String key, Object object) {
        data.put(key, object);
        return this;
    }

    public Result putData(Map<String, ?> map) {
        data.putAll(map);
        return this;
    }

    public Result putData(Collection<?> list) {
        data.put("list", list);
        return this;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public Result setSuccess(Boolean success) {
        isSuccess = success;
        return this;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result putData(Object object) {
        ArrayList<Class<?>> classList = new ArrayList<>();
        data.putAll(convertObjectToMap(object, classList));
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "isSuccess=" + isSuccess +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
