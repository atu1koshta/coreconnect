package com.unemployed.coreconnect.service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.dto.OnlineUserInfo;

@Service
@EnableScheduling
public class HeartbeatService {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ConcurrentMap<String, OnlineUserInfo> onlineDevices = new ConcurrentHashMap<>();

    public Collection<OnlineUserInfo> getOnlineDevices() {
        return this.onlineDevices.values();
    }

    @SuppressWarnings("null")
    public boolean updateOnlineStates(String ipAddress, String macAddress, String username) {

        long currentTimeMillis = System.currentTimeMillis();
        OnlineUserInfo onlineDeviceInfo;
        if (!onlineDevices.containsKey(username)) {
            onlineDeviceInfo = new OnlineUserInfo(ipAddress, username, currentTimeMillis);
            onlineDevices.put(username, onlineDeviceInfo);
            broadcastOnlineDevices();
        } else {
            onlineDeviceInfo = onlineDevices.get(username);
            onlineDeviceInfo.setLastHeartbeat(currentTimeMillis);
        }
        onlineDeviceInfo.setHeartbeatReceived(true);

        return true;
    }

    @Scheduled(fixedRate = Constant.WebSocket.HEARTBEAT_INTERVAL)
    public void checkHeartbeats() {
        log.info("Checking heartbeats...");

        long currentTimeMillis = System.currentTimeMillis();

        onlineDevices.forEach((username, onlineDeviceInfo) -> {
            if (!onlineDeviceInfo.isHeartbeatReceived()) {
                onlineDeviceInfo.incrementNoHeartbeatCount();
            } else {
                onlineDeviceInfo.resetNoHeartbeatCount();
            }

            if (onlineDeviceInfo.isOffline(currentTimeMillis)) {
                onlineDevices.remove(username);

                log.info("User " + username + " is offline");

                broadcastOnlineDevices();
            }
            onlineDeviceInfo.setHeartbeatReceived(false);
        });
    }

    private void broadcastOnlineDevices() {
        messagingTemplate.convertAndSend(Constant.WebSocket.ONLINE_USERS, onlineDevices.values());
    }
}
