// package com.choose.service.aiModel;
//
//
// import com.choose.ai.ChatModelProperties;
// import com.choose.ai.pojo.OpenAIConfig;
// import dev.langchain4j.model.chat.ChatLanguageModel;
// import dev.langchain4j.model.chat.StreamingChatLanguageModel;
// import dev.langchain4j.model.embedding.EmbeddingModel;
// import dev.langchain4j.model.openai.OpenAiChatModel;
// import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
// import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
//
//
// public class OpenAiLLMService extends AbstractLLMService<OpenAIConfig> {
//
//     public OpenAiLLMService(String modelName, OpenAIConfig config) {
//         super(modelName, config);
//     }
//
//     @Override
//     public ChatLanguageModel buildChatModel(ChatModelProperties properties) {
//         return OpenAiChatModel.builder()
//                 .apiKey(config.getApiKey())
//                 .modelName(modelName)
//                 .temperature(properties.getTemperature())
//                 .timeout(config.getTimeout())
//                 .build();
//     }
//
//     @Override
//     public StreamingChatLanguageModel buildStreamingModel(ChatModelProperties properties) {
//         return OpenAiStreamingChatModel.builder()
//                 .apiKey(config.getApiKey())
//                 .modelName(modelName)
//                 .temperature(properties.getTemperature())
//                 .timeout(config.getTimeout())
//                 .build();
//     }
//
//     @Override
//     public EmbeddingModel getEmbeddingModel() {
//         return OpenAiEmbeddingModel.withApiKey(config.getApiKey());
//     }
// }