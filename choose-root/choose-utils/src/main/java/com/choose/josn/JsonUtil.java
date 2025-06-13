package com.choose.josn;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.annotations.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 桌角的眼镜
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static String prettyFormat(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
    }

    public static <T> T toInstance(Object obj, Class<T> clazz) {
        if (obj instanceof cn.hutool.json.JSONObject huToolJSONObject) {
            return huToolJSONObject.toBean(clazz);
        }
        String json = toJson(obj);
        return toInstance(json, clazz);
    }

    public static <T> T toInstance(String jsonString, Class<T> clazz) {
        return JSONObject.parseObject(jsonString, clazz, JSONReader.Feature.SupportSmartMatch);
    }


    public static <T> List<T> toList(String json, Class<T> valueClazz) throws Exception {
        ArrayList<T> clazz = null;
        try {
            clazz = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(StrFormatter.format("Json to List Failed, value class is {}", valueClazz.getName()));
            throw new Exception(e);
        }
        return clazz;
    }

    public static <T, M> HashMap<T, M> toMap(Object obj, Class<T> keyClazz, Class<M> valueClazz) throws Exception {
        String json = toJson(obj);
        return toMap(json, keyClazz, valueClazz);
    }

    public static <T, M> HashMap<T, M> toMap(String json, Class<T> keyClazz, Class<M> valueClazz) throws Exception {
        HashMap<T, M> clazz = null;
        try {
            clazz = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(StrFormatter.format("Json to Map Failed, key class is {}, value class is {}", keyClazz.getName(), valueClazz.getName()));
            // throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            throw new Exception(e);
        }

        return clazz;
    }

    public static HashMap<String, Object> toMap(Object obj) throws Exception {
        String json = toJson(obj);
        return toMap(json);
    }

    public static HashMap<String, Object> toMap(String json) throws Exception {
        HashMap<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(StrFormatter.format("current Object is {}, message is {}", json, e.getMessage()));
            // throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            throw new Exception(e);
        }

        return map;
    }

    public static <T, M> Map<T, M> toMap(String json, TypeReference<Map<T, M>> typeReference) throws Exception {
        try {
            return new ObjectMapper().readValue(json, typeReference);
        } catch (Exception e) {
            log.error("Json to Map Failed", e);
            // throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            throw new Exception(e);
        }
    }

    public static <T> void validateJson(Object obj, Class<T> clazz, @Nullable String path) throws Exception {
        if (path == null) {
            path = clazz.getName();
        }
        Set<String> classFields = getClassFields(clazz);
        String json = toJson(obj);
        HashMap<String, Object> map = toMap(json);

        for (String key : map.keySet()) {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            if (!classFields.contains(key)) {
                log.error(StrFormatter.format("Extra field not found at path: {}", currentPath));
            }
            Object value = map.get(key);
            if (value instanceof HashMap<?, ?>) {
                validateJson(value, value.getClass(), path);
            }
        }

    }

    private static Set<String> getClassFields(Class<?> clazz) {
        Set<String> fieldNames = new HashSet<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }


    /**
     * JSON对象反序列化
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            JsonParser jp = getParser(json);
            return jp.readValueAs(clazz);
        } catch (JsonParseException jpe) {
            log.error("反序列化失败", jpe);
        } catch (JsonMappingException jme) {
            log.error("反序列化失败", jme);
        } catch (IOException ioe) {
            log.error("反序列化失败", ioe);
        }
        return null;
    }


    /**
     * 创建JSON处理器的静态方法
     *
     * @param content JSON字符串
     * @return
     */
    private static JsonParser getParser(String content) {
        if (StringUtils.isNotBlank(content)) {
            try {
                return objectMapper.getFactory().createParser(content);
            } catch (IOException ioe) {
                log.error("JsonUtil getParser error", ioe);
            }
        }
        return null;
    }
}
