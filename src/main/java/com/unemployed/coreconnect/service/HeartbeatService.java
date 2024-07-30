package com.unemployed.coreconnect.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.DeviceInfo;

@Service
@EnableScheduling
public class HeartbeatService {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	private ConcurrentMap<String, DeviceInfo> onlineDevices = new ConcurrentHashMap<>();

	@SuppressWarnings("null")
	public boolean updateDevicesState(SimpMessageHeaderAccessor headerAccessor) {
		if (headerAccessor.getSessionAttributes() == null) {
			return false;
		}
		
		String ipAddress = headerAccessor.getSessionAttributes().get("ipAddress").toString();
		String macAddress = headerAccessor.getSessionAttributes().get("macAddress").toString();
		String deviceName = headerAccessor.getSessionAttributes().get("deviceName").toString();

		long currentTimeMillis = System.currentTimeMillis();
		DeviceInfo deviceInfo;
		if (!onlineDevices.containsKey(macAddress)) {
			deviceInfo = new DeviceInfo(ipAddress, deviceName, currentTimeMillis);
			onlineDevices.put(macAddress, deviceInfo);
			broadcastOnlineDevices();
		} else {
			deviceInfo = onlineDevices.get(macAddress);
			deviceInfo.setLastHeartbeat(currentTimeMillis);
		}
		deviceInfo.setHeartbeatReceived(true);
		
		return true;
	}

	@Scheduled(fixedRate = Constant.WebSocket.HEARTBEAT_INTERVAL) // Check every 10 seconds
	public void checkHeartbeats() {
		System.out.println("Checking heartbeats...");
		long currentTimeMillis = System.currentTimeMillis();

		onlineDevices.forEach((macAddress, deviceInfo) -> {
			if (!deviceInfo.isHeartbeatReceived()) {
				deviceInfo.incrementNoHeartbeatCount();
			}

			if (deviceInfo.isOffline(currentTimeMillis)) {
				onlineDevices.remove(macAddress);
				System.out.println("Device " + macAddress + " is offline");
				broadcastOnlineDevices();
			}
			deviceInfo.setHeartbeatReceived(false);
		});
	}

	private void broadcastOnlineDevices() {
		messagingTemplate.convertAndSend(Constant.WebSocket.ONLINE_DEVICES, onlineDevices.values());
	}
}
