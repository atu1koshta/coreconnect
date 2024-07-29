package com.unemployed.coreconnect.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.UdpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
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
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.service.DeviceService;
import com.unemployed.coreconnect.service.NetworkService;
import com.unemployed.coreconnect.service.SnmpFetcher;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Autowired
	private SnmpFetcher snmpFetcher;

	@Autowired
	private DeviceService deviceService;

	private ConcurrentHashMap<String, String> onlineUsers = new ConcurrentHashMap<>();

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("chat");
		registry.addEndpoint("/chat").withSockJS().setInterceptors(new HandshakeInterceptor() {

			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

				String ipAddress = request.getRemoteAddress().getAddress().toString().substring(1);
				if (ipAddress.equals(Constant.Network.LOCAL_IPV6)) {
					ipAddress = NetworkService.getHostLocalIp();
				}

				ResponseEvent<UdpAddress> responseEvent = snmpFetcher.fetchSnmpData();
				String macAddress = snmpFetcher.getMacAddressForIp(responseEvent, ipAddress);

				Pair<Boolean, String> result = deviceService.checkAndRegisterDevice(macAddress);
				attributes.put("deviceName", result.getSecond());
				attributes.put("ipAddress", ipAddress);
				
				if (result.getFirst()) {
                    onlineUsers.put(ipAddress, result.getSecond());
                }

				return result.getFirst();
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				return;
			}

		});
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// TODO Auto-generated method stub
		registry.enableSimpleBroker("/topic", "/queue");
		registry.setApplicationDestinationPrefixes("/app");
		registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
			@Override
			public WebSocketHandler decorate(WebSocketHandler handler) {
				return new WebSocketHandlerDecorator(handler) {
					@Override
					public void afterConnectionEstablished(WebSocketSession session) throws Exception {
						String deviceName = (String) session.getAttributes().get("deviceName");
						System.out.println("Welcome " + deviceName);

						super.afterConnectionEstablished(session);
					}

					@Override
					public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
							throws Exception {
						super.afterConnectionClosed(session, closeStatus);
					}
				};
			}
		});
	}
	
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        ConcurrentHashMap<String, String> simpSessionAttributes =  (ConcurrentHashMap<String, String>) event.getMessage().getHeaders().get("simpSessionAttributes");
        String disconnectedIpAddress = simpSessionAttributes.get("ipAddress");
        onlineUsers.remove(disconnectedIpAddress);
        
    }

    public ConcurrentHashMap<String, String> getOnlineUsers() {
        return onlineUsers;
    }
    
    @Scheduled(fixedRate = 50000)
	public void printOnlineUsers() {
		System.out.println("Online users: \n" + onlineUsers);
		System.out.println();
	}
    
}
