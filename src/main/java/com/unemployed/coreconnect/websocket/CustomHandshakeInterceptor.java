package com.unemployed.coreconnect.websocket;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.unemployed.coreconnect.model.entity.Device;
import com.unemployed.coreconnect.service.NetworkService;

@Configuration
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CustomHandshakeInterceptor.class);

    @Autowired
    private NetworkService networkService;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {

        String ipAddress = networkService.getLocalIpAddressFromRequest(request);
        Device device = networkService.getDeviceFromIpAddress(ipAddress);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && device != null) {
            String username = authentication.getName();
            attributes.put("username", username);
            attributes.put("id", device.getId());
            attributes.put("deviceName", device.getDeviceName());
            attributes.put("ipAddress", ipAddress);
            attributes.put("macAddress", device.getMacAddress());

            log.info("User " + username + " has connected from device " + device.getDeviceName() + " with IP: "
                    + ipAddress);
            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
