package com.unemployed.coreconnect.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.websocket.CustomHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(Constant.WebSocket.TOPIC, Constant.WebSocket.QUEUE);
        registry.setApplicationDestinationPrefixes(Constant.WebSocket.APP);
        registry.setUserDestinationPrefix(Constant.WebSocket.USER);
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint(Constant.WebSocket.CHAT).setAllowedOrigins("*");
        registry.addEndpoint(Constant.WebSocket.CHAT).withSockJS().setInterceptors(customHandshakeInterceptor);
    }

    @Override
    public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory((@NonNull WebSocketHandler handler) -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
                String deviceIpAddress = (String) session.getAttributes().get("ipAddress");
                String deviceName = (String) session.getAttributes().get("deviceName");

                log.info("Device connected: " + deviceName + " with IP: " + deviceIpAddress);

                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(@NonNull WebSocketSession session,
                    @NonNull CloseStatus closeStatus) throws Exception {
                super.afterConnectionClosed(session, closeStatus);
            }
        });
    }
}
