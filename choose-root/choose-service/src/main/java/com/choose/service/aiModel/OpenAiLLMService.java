package com.choose.service.aiModel;


import com.choose.ai.pojo.AiModel;
import com.choose.ai.pojo.LLMBuilderProperties;
import com.choose.ai.pojo.OpenAiSetting;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.stringPlus.StringPlusUtils;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.net.ProxySelector;
import java.net.http.HttpClient;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/13 16:19
 */
public class OpenAiLLMService extends AbstractLLMService<OpenAiSetting> {

    public OpenAiLLMService(AiModel model) {
        super(model, OpenAiSetting.class);
    }

    @Override
    public ChatLanguageModel buildChatModel(LLMBuilderProperties properties) {
        if(StringPlusUtils.isBlank(modelPlatformSetting.getSecretKey())) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .baseUrl(modelPlatformSetting.getBaseUrl())
                .modelName(aiModel.getName())
                .temperature(properties.getTemperature())
                .apiKey(modelPlatformSetting.getSecretKey());
        if(StringUtils.isNotBlank(modelPlatformSetting.getBaseUrl())) {
            builder.baseUrl(modelPlatformSetting.getBaseUrl());
        }

        if(StringUtils.isNotBlank(modelPlatformSetting.getSecretKey())) {
            builder.baseUrl(modelPlatformSetting.getBaseUrl());
        }
        // todo 补充代理
        // if (null != proxyAddress) {
        //     HttpClient.Builder httpClientBuilder = HttpClient.newBuilder().proxy(ProxySelector.of(proxyAddress));
        //     builder.httpClientBuilder(JdkHttpClient.builder().httpClientBuilder(httpClientBuilder));
        // }
        return builder.build();
    }

    @Override
    public StreamingChatLanguageModel buildStreamingModel(LLMBuilderProperties properties) {
        return null;
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        return null;
    }
}