package com.choose.controller.ai;

import com.choose.ai.dto.AskReq;
import com.choose.service.aiModel.ConversationMessageService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

@RestController
@RequiredArgsConstructor
@RequestMapping("/choose/ai")
public class AiChatController {

    @Value("${langchain4j.dashscope.api-key}")
    private String apiKey;

    @GetMapping(value = "/v1/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(String message) {
        // 创建SSE发射器
        SseEmitter emitter = new SseEmitter();

        // 创建流式聊天模型
        QwenStreamingChatModel model = QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-turbo")
                .temperature(0.7f)
                .build();

        // 发送用户消息并处理流式响应
        model.generate(UserMessage.from(message), new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                try {
                    // 发送每个token到客户端
                    emitter.send(SseEmitter.event().data(token));
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                emitter.complete();
            }

            @Override
            public void onError(Throwable error) {
                emitter.completeWithError(error);
            }
        });

        return emitter;
    }

    @Resource
    private ConversationMessageService conversationMessageService;

    @PostMapping(value = "/chat")
    public String chat(@RequestBody AskReq askReq) {
        return conversationMessageService.chatAsk(askReq);
    }

    @PostMapping(value = "/stream/chat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody AskReq askReq) {
        SseEmitter sseEmitter = new SseEmitter();

        return conversationMessageService.sseAsk(askReq);
    }

    // @PostMapping("/rag")
    // public String rag(@RequestParam String modelName,
    //                   @RequestBody String question) {
    //     return ragService.query(modelName, question);
    // }
}