package com.choose.service.im.impl;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * @author 桌角的眼镜
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private NotificationWebSocketHandlerServer notificationWebSocketHandlerServer;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 前端链接 const socket = new WebSocket('ws://localhost/choose-websocket?userId=123');
        registry.addHandler(notificationWebSocketHandlerServer, "/choose-websocket")
                .setAllowedOrigins("*");
    }
}