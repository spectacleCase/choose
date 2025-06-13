package com.choose.service.aiModel;

import com.choose.ai.pojo.AiModel;
import com.choose.ai.pojo.LLMBuilderProperties;
import com.choose.josn.JsonUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Getter;

@Getter
public abstract class AbstractLLMService<T> {


    @Getter
    protected AiModel aiModel;
    protected T modelPlatformSetting;

    public AbstractLLMService(AiModel aiModel,Class<T> clazz) {
        this.aiModel = aiModel;
        this.modelPlatformSetting =  JsonUtil.fromJson(aiModel.getSetting(), clazz);
    }

    public abstract ChatLanguageModel buildChatModel(LLMBuilderProperties properties);

    public abstract StreamingChatLanguageModel buildStreamingModel(LLMBuilderProperties properties);

    public abstract EmbeddingModel getEmbeddingModel();

    // public boolean isEnabled() {
    //     return config != null && StringUtils.hasText(config.getApiKey());
    // }

    // public Retriever<Document> buildRetriever(List<EmbeddingStoreRetriever> retrievers) {
    //     return new AggregatingRetriever(retrievers);
    // }
}