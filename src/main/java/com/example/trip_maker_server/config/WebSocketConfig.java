package com.example.trip_maker_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 받을 때 (구독): /sub
        config.enableSimpleBroker("/sub");
        // 메시지 보낼 때 (발행): /pub
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // (★수정) 1. 기본 WebSocket 연결 허용
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*");
                
        // (★수정) 2. SockJS 지원 연결 허용 (프론트에서 이걸 씀)
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS(); 
    }
}