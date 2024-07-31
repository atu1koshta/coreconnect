package com.unemployed.coreconnect.websocket.config;

import java.util.Map;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.UdpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
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

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.Device;
import com.unemployed.coreconnect.service.DeviceService;
import com.unemployed.coreconnect.service.NetworkService;
import com.unemployed.coreconnect.service.SnmpFetcher;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Autowired
	private SnmpFetcher snmpFetcher;

	@Autowired
	private DeviceService deviceService;

	@Override
	public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
		registry.enableSimpleBroker(Constant.WebSocket.TOPIC, Constant.WebSocket.QUEUE);
		registry.setApplicationDestinationPrefixes(Constant.WebSocket.APP);
		registry.setUserDestinationPrefix(Constant.WebSocket.USER);
	}

	@Override
	public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
		registry.addEndpoint(Constant.WebSocket.CHAT).setAllowedOrigins("*");
		registry.addEndpoint(Constant.WebSocket.CHAT).withSockJS().setInterceptors(new HandshakeInterceptor() {

			@Override
			public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
					@NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {

				String ipAddress = request.getRemoteAddress().getAddress().toString().substring(1);
				if (ipAddress.equals(Constant.Network.LOCAL_IPV6)) {
					ipAddress = NetworkService.getHostLocalIp();
				}

				ResponseEvent<UdpAddress> responseEvent = snmpFetcher.fetchSnmpData();
				String macAddress = snmpFetcher.getMacAddressForIp(responseEvent, ipAddress);

				Device device = deviceService.checkAndRegisterDevice(macAddress);
				if (device == null) {
					return false;
				}
				
				attributes.put("id", device.getId());
				attributes.put("deviceName", device.getDeviceName());
				attributes.put("ipAddress", ipAddress);
				attributes.put("macAddress", macAddress);

				return true;
			}

			@Override
			public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
					@NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registration) {
		registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
			@Override
			public @NonNull WebSocketHandler decorate(@NonNull WebSocketHandler handler) {
				return new WebSocketHandlerDecorator(handler) {
					@Override
					public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
						String deviceIpAddress = (String) session.getAttributes().get("ipAddress");
						String deviceName = (String) session.getAttributes().get("deviceName");
						System.out.println("Device connected: " + deviceName + " with IP: " + deviceIpAddress);

						super.afterConnectionEstablished(session);
					}

					@Override
					public void afterConnectionClosed(@NonNull WebSocketSession session,
							@NonNull CloseStatus closeStatus) throws Exception {
						super.afterConnectionClosed(session, closeStatus);
					}
				};
			}
		});
	}
}
