package com.unemployed.coreconnect.config;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.UdpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.unemployed.coreconnect.service.DeviceService;
import com.unemployed.coreconnect.service.SnmpFetcher;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Autowired
	private SnmpFetcher snmpFetcher;
	
	@Autowired
	private DeviceService deviceService;
	
	private AtomicInteger connectionCount = new AtomicInteger(0);

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("chat");
		registry.addEndpoint("/chat").withSockJS().setInterceptors(new HandshakeInterceptor() {

			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
				connectionCount.incrementAndGet();
				
				String ipAddress = request.getRemoteAddress().getAddress().toString();
				ResponseEvent<UdpAddress> responseEvent = snmpFetcher.fetchSnmpData();
				String macAddress = snmpFetcher.getMacAddressForIp(responseEvent, ipAddress.substring(1));
				
				Pair<Boolean, String> result = deviceService.checkAndRegisterDevice(macAddress);
				attributes.put("deviceName" , result.getSecond());
				
				return result.getFirst();
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				connectionCount.decrementAndGet();
			}
			
			
		});
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// TODO Auto-generated method stub
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
			@Override
			public WebSocketHandler decorate(WebSocketHandler handler) {
				return new WebSocketHandlerDecorator(handler) {
					@Override
					public void afterConnectionEstablished(WebSocketSession session) throws Exception {
						connectionCount.incrementAndGet();
						String deviceName = (String) session.getAttributes().get("deviceName");
						System.out.println("Welcome " + deviceName);
						
						super.afterConnectionEstablished(session);
					}

					@Override
					public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
							throws Exception {
						connectionCount.decrementAndGet();
						super.afterConnectionClosed(session, closeStatus);
					}
				};
			}
		});
	}

	@Scheduled(fixedRate = 10000)
	public void reportCurrentTime() {
		System.out.println("Number of alive WebSocket connections: " + connectionCount.get());
	}

}
