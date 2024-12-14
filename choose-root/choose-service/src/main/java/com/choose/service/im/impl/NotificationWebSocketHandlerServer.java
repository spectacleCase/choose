package com.choose.service.im.impl;

import com.choose.enums.ChatEnum;
import com.choose.im.pojos.ChatMessage;
import com.choose.im.pojos.ImMessage;
import com.choose.mapper.ChatMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * websocket Im
 *
 * @author 桌角的眼镜
 */

@Slf4j
@Service
public class NotificationWebSocketHandlerServer extends TextWebSocketHandler {

    public final Map<Long, WebSocketSession> USER_SESSIONS = new HashMap<>();

    @Resource
    private ChatMapper chatMapper;


    /**
     * 链接
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // 获取用户的ID
        Long userId = getUserIdFromSession(session);
        log.info("{}连接了", userId);
        USER_SESSIONS.put(userId, session);
    }

    /**
     * 处理信息
     */
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        System.out.println("Message received: " + message.getPayload());
        String a = "{\"messageType\":\"heartbeat\"}";
        try {
            if (a.equals(message.getPayload())) {
                return;
            }
            ImMessage parsedMessage = new Gson().fromJson(message.getPayload(), ImMessage.class);
            WebSocketSession receiverSession = USER_SESSIONS.get(Long.valueOf(parsedMessage.getReceiver()));
            // 如果接收者的会话存在且处于打开状态，则发送消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(parsedMessage.getContent());
            chatMessage.setSender(Long.valueOf(parsedMessage.getSender()));
            chatMessage.setReceiver(Long.valueOf(parsedMessage.getReceiver()));
            // chatMessage.setType(ChatEnum.getCode(parsedMessage.getType()));
            chatMessage.setType(parsedMessage.getType());

            // if (receiverSession != null && receiverSession.isOpen()) {
            //     chatMessage.setIsRead(1);
            // } else {
            chatMessage.setIsRead(0);
            // }
            chatMapper.insert(chatMessage);
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(message);
            }
        } catch (IOException e) {
            log.error("onMessage{}", e.getMessage());
        }
    }

    /**
     * 断开链接
     */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Long userId = getUserIdFromSession(session);
        log.info("{}断开了", userId);
        USER_SESSIONS.remove(userId);
    }


    /**
     * 会话中获取用户Id
     */
    private Long getUserIdFromSession(WebSocketSession session) throws Exception {
        String[] split = Objects.requireNonNull(session.getUri()).getQuery().split("=");
        if (split.length != 2) {
            // throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
            throw new Exception();
        }
        return Long.valueOf(split[1]);
    }


    public void sendComment(Long userId, String message) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("sendComment{}", e.getMessage());
            }
        }
    }

    public void sendNotification(Long userId, String message) {
        Session session = (Session) USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText("[NOTIFICATION] " + message);
            } catch (Exception e) {
                log.error("sendNotification{}", e.getMessage());
            }
        }
    }


    public void sendCommentReply(Long userId, String message) {
        Session session = (Session) USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText("[REPLY] " + message);
            } catch (Exception e) {
                log.error("sendCommentReply{}", e.getMessage());
            }
        }
    }
}