package com.example.fjm0313_takeout_self.common.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SellerOrderWebSocketHandler implements WebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("商家端 WebSocket 连接成功，当前在线商家数量：" + sessions.size());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        System.out.println("收到商家端 WebSocket 消息：" + message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessions.remove(session);
        System.out.println("商家端 WebSocket 连接异常：" + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessions.remove(session);
        System.out.println("商家端 WebSocket 连接关闭，当前在线商家数量：" + sessions.size());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendToAllSellers(Object data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            TextMessage textMessage = new TextMessage(json);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }

            System.out.println("已向商家端推送新订单消息：" + json);
        } catch (Exception e) {
            System.out.println("推送商家端 WebSocket 消息失败：" + e.getMessage());
        }
    }
}