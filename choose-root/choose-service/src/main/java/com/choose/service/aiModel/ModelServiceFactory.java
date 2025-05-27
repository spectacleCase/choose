package com.choose.service.aiModel;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/9 21:04
 */
@Component
public class ModelServiceFactory {
    private final Map<String, ModelService<?>> modelServices;

    public ModelServiceFactory(Map<String, ModelService<?>> modelServices) {
        this.modelServices = modelServices;
    }

    public ModelService<?> getService(String modelName) {
        ModelService<?> service = modelServices.get(modelName + "Service");
        if (service == null) {
            throw new IllegalArgumentException("未找到模型服务: " + modelName);
        }
        return service;
    }
}