package com.choose.service.aiModel;

import com.choose.ai.pojo.AiModel;
import com.choose.ai.pojo.DashScopeSetting;
import com.choose.ai.pojo.LLMBuilderProperties;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/13 16:19
 */
public class DashScopeLLMService extends AbstractLLMService<DashScopeSetting> {

    public DashScopeLLMService(AiModel aiModel) {
        super(aiModel, DashScopeSetting.class);
    }

    @Override
    public ChatLanguageModel buildChatModel(LLMBuilderProperties properties) {
        if (StringUtils.isBlank(modelPlatformSetting.getApiKey())) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        float temperature = properties.getTemperatureWithDefault(0.7).floatValue();
        return QwenChatModel.builder()
                .apiKey(modelPlatformSetting.getApiKey())
                .modelName(aiModel.getName())
                .temperature(temperature)
                .build();
    }

    @Override
    public StreamingChatLanguageModel buildStreamingModel(LLMBuilderProperties properties) {
        return QwenStreamingChatModel.builder()
                .apiKey(modelPlatformSetting.getApiKey())
                .modelName(aiModel.getName())
                .temperature(properties.getTemperature().floatValue())
                .build();

    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        return QwenEmbeddingModel.builder().apiKey(modelPlatformSetting.getApiKey()).build();
    }
}
