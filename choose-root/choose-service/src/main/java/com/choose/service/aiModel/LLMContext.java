package com.choose.service.aiModel;


import com.choose.ai.pojo.AiModel;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Service
public class LLMContext {

    private static final Map<String, AbstractLLMService<?>> NAME_TO_LLM_SERVICE = new HashMap<>();

    public LLMContext() {
    }


    public static void addLLMService(AbstractLLMService<?> llmService) {
        NAME_TO_LLM_SERVICE.put(llmService.getAiModel().getName(), llmService);
    }

    public void registerService(String modelName, AbstractLLMService<?> service) {
        NAME_TO_LLM_SERVICE.put(modelName, service);
    }



    public static AbstractLLMService<?> getLLMServiceByName(String modelName) {
        AbstractLLMService<?> service = NAME_TO_LLM_SERVICE.get(modelName);
        if (null == service) {
            Optional<AbstractLLMService<?>> serviceOptional = getFirstEnableAndFree();
            if (serviceOptional.isPresent()) {
                log.warn("︿︿︿ 找不到 {},使用第1个可用的免费模型 {} ︿︿︿", modelName, serviceOptional.get().getAiModel().getName());
                return serviceOptional.get();
            }
            log.error("︿︿︿ 没有可用的模型,请检查平台及模型的配置 ︿︿︿");
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        return service;
    }

    /**
     * 选择顺序：
     * 一、优先选择免费可用的模型；
     * 二、收费可用的模型
     *
     * @return 返回免费可用或收费可用的模型
     */
    public static Optional<AbstractLLMService<?>> getFirstEnableAndFree() {
        return NAME_TO_LLM_SERVICE.values().stream().filter(item -> {
            AiModel aiModel = item.getAiModel();
            if (aiModel.getIsEnable() && aiModel.getIsFree()) {
                return true;
            } else return Boolean.TRUE.equals(aiModel.getIsEnable());
        }).findFirst();
    }

    public AbstractLLMService<?> getService(String modelName) {
        return Optional.ofNullable(NAME_TO_LLM_SERVICE.get(modelName))
                .orElseThrow(() -> new RuntimeException("Model not found: " + modelName));
    }
}