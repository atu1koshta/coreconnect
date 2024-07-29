package com.unemployed.coreconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.DeviceInfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
public class HeartbeatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ConcurrentMap<String, DeviceInfo> onlineDevices = new ConcurrentHashMap<>();

    @SuppressWarnings("null")
	@MessageMapping(Constant.WebSocket.HEARTBEAT)
    public void receiveHeartbeat(SimpMessageHeaderAccessor headerAccessor) {
    	System.out.println("Received heartbeat...");
    	if (headerAccessor.getSessionAttributes() != null) {
            String ipAddress = headerAccessor.getSessionAttributes().get("ipAddress").toString();
			String macAddress = headerAccessor.getSessionAttributes().get("macAddress").toString();
			String deviceName = headerAccessor.getSessionAttributes().get("deviceName").toString();
            
            long currentTimeMillis = System.currentTimeMillis();
            if (!onlineDevices.containsKey(macAddress)) {
            	DeviceInfo deviceInfo = new DeviceInfo(ipAddress, deviceName, currentTimeMillis);
                onlineDevices.put(macAddress, deviceInfo);
                broadcastOnlineDevices();
            } else {
                onlineDevices.get(macAddress).setLastHeartbeat(currentTimeMillis);
            }
    	}
    }


    @Scheduled(fixedRate = Constant.WebSocket.HEARTBEAT_INTERVAL) // Check every 10 seconds
    public void checkHeartbeats() {
    	System.out.println("Checking heartbeats...");
    	long currentTimeMillis = System.currentTimeMillis();
    	
        onlineDevices.forEach((macAddress, deviceInfo) -> {
            if (deviceInfo.isOffline(currentTimeMillis)) {
                onlineDevices.remove(macAddress);
                System.out.println("Device " + macAddress + " is offline");
                broadcastOnlineDevices();
            }
        });
    }

    private void broadcastOnlineDevices() {
        messagingTemplate.convertAndSend(Constant.WebSocket.ONLINE_DEVICES, onlineDevices.values());
    }
}
