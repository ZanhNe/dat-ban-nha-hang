package com.ou.nhahang.dat_ban_nha_hang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để client kết nối WebSocket
        registry.addEndpoint("/ws-booking")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix này dùng để bên phía client subcribe vào cái channel của server
        // topic cho public nha mấy cha
        // còn queue thì cho cá nhân
        registry.enableSimpleBroker("/topic", "/queue");
        // Định nghĩa prefix cho các message gửi từ client
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix dành riêng cho thông báo cá nhân (Spring Security sẽ xử lý)
        registry.setUserDestinationPrefix("/user");

    }
}
