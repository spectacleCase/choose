package com.choose.service.aiModel;

import com.choose.ai.dto.AskReq;
import com.choose.ai.pojo.LLMBuilderProperties;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/13 18:31
 */
@Service
public class ConversationMessageService {


    public String chatAsk(AskReq askReq) {

        ChatLanguageModel model = LLMContext.getLLMServiceByName(askReq.getModelName()).buildChatModel(new LLMBuilderProperties());

        return model.generate(askReq.getPrompt());
    }

    public SseEmitter sseAsk(AskReq askReq) {

        return new  SseEmitter();
    }

}
