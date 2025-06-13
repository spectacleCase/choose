package com.choose.service.aiModel;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final LLMContext llmContext;
    private final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();



    public String query(String modelName, String question) {
        var llmService = llmContext.getService(modelName);
        var embeddingModel = llmService.getEmbeddingModel();

        // 1. 生成问题向量
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // 2. 检索相关文档
        List<EmbeddingMatch<TextSegment>> results = embeddingStore.findRelevant(
                questionEmbedding,
                5, 0.6
        );

        // 3. 构建上下文
        String context = results.stream()
                .map(r -> r.embedded().text())
                .reduce("", (a, b) -> a + "\n\n" + b);

        // 4. 调用LLM生成回答
        return llmService.buildChatModel(null)
                .generate("基于以下上下文回答问题:\n" + context + "\n\n问题:" + question);
    }
}