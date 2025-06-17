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
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.apache.commons.lang.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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

    // /**
    //  * 兼容OpenAi的模型，重新指定系统配置项，并使用本构造器进行初始化
    //  *
    //  * @param model        adi_ai_model中的模型
    //  * @param sysConfigKey 系统配置项名称，如DeepSeek兼容openai的api格式，DeepSeek的系统配置项在adi_sys_config中为deepseek_setting
    //  */
    // public OpenAiLLMService(AiModel model, String sysConfigKey) {
    //     // super(model, sysConfigKey, OpenAiSetting.class);
    //     super(model, OpenAiSetting.class);
    // }

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
        if (org.apache.commons.lang3.StringUtils.isNotBlank(modelPlatformSetting.getBaseUrl())) {
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
        if(StringUtils.isNotBlank(modelPlatformSetting.getSecretKey())) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        Double temperature = properties.getTemperatureWithDefault(0.7);
        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
                .baseUrl(modelPlatformSetting.getBaseUrl())
                .modelName(aiModel.getName())
                .temperature(temperature)
                .apiKey(modelPlatformSetting.getSecretKey())
                .timeout(Duration.of(60, ChronoUnit.SECONDS));
        // if(null != proxyAddress ) {

        // }


        return builder.build();
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        return null;
    }
}