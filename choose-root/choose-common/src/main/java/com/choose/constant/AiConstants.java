package com.choose.constant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/13 17:54
 */
public class AiConstants {



    public static class ModelType {
        public ModelType() {
        }

        public static final String TEXT = "text";
        public static final String IMAGE = "image";
        public static final String EMBEDDING = "embedding";
        public static final String RERANK = "rerank";
    }

    public static class ModelPlatform {
        private ModelPlatform() {
        }

        public static final String DEEPSEEK = "deepseek";
        public static final String OPENAI = "openai";
        public static final String DASHSCOPE = "dashscope";
        public static final String QIANFAN = "qianfan";
        public static final String OLLAMA = "ollama";
        public static final String SILICONFLOW = "siliconflow";

        // 获取所有公共静态常量（String类型的值）的列表
        public static List<String> getModelConstants() {
            List<String> list = new ArrayList<>();
            Class<ModelPlatform> clazz = ModelPlatform.class;
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    String value = (String) field.get(null);
                    list.add(value);
                } catch (ReflectiveOperationException e) {
                }

            }
            return list;
        }
    }
}
