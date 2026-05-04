package com.example.fjm0313_takeout_self.config;

import com.example.fjm0313_takeout_self.common.websocket.SellerOrderWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SellerOrderWebSocketHandler sellerOrderWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sellerOrderWebSocketHandler, "/ws/seller/orders")
                .setAllowedOriginPatterns("*");
    }
}