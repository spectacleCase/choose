package com.choose.service.textAbstract;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;


/**
 * <p>
 * SseEmitterUTF8
 *  重写SseEmitter 改为UTF-8编码
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/7 上午10:56
 */
public class SseEmitterUTF8 extends SseEmitter {

    public SseEmitterUTF8(Long timeout) {
        super(timeout);
    }

    @Override
    protected void extendResponse(@NotNull ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);

        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType( new MediaType("text", "event-stream", StandardCharsets.UTF_8));
    }
}

