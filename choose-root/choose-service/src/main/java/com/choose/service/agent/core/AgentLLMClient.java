package com.choose.service.agent.core;

import com.choose.service.aiModel.DeepSeekV3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Agent专用的LLM调用入口,封装DeepSeekV3Service作为底座。
 * 后续若要按论文2.1.1按Agent分配不同模型(Qwen/GLM/DeepSeek), 在这里加分支即可。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AgentLLMClient {

    private final DeepSeekV3Service<Object> deepSeekV3Service;

    /**
     * 同步对话,返回最终文本。失败抛运行时异常,由BaseAgent捕获。
     */
    public String chat(String systemPrompt, String userPrompt) {
        StringBuilder out = deepSeekV3Service.extracted(systemPrompt, userPrompt);
        if (out == null) {
            throw new RuntimeException("LLM返回空");
        }
        return out.toString();
    }
}
