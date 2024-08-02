package com.unemployed.coreconnect.service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.DeviceInfo;
import com.unemployed.coreconnect.utils.Logging;

@Service
@EnableScheduling
public class HeartbeatService implements Logging {

    private final Logger log = getLogger();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ConcurrentMap<String, DeviceInfo> onlineDevices = new ConcurrentHashMap<>();

    public Collection<DeviceInfo> getOnlineDevices() {
        return this.onlineDevices.values();
    }

    @SuppressWarnings("null")
    public boolean updateDevicesState(int id, String ipAddress, String macAddress, String deviceName) {

        long currentTimeMillis = System.currentTimeMillis();
        DeviceInfo deviceInfo;
        if (!onlineDevices.containsKey(macAddress)) {
            deviceInfo = new DeviceInfo(id, ipAddress, deviceName, currentTimeMillis);
            onlineDevices.put(macAddress, deviceInfo);
            broadcastOnlineDevices();
        } else {
            deviceInfo = onlineDevices.get(macAddress);
            deviceInfo.setLastHeartbeat(currentTimeMillis);
        }
        deviceInfo.setHeartbeatReceived(true);

        return true;
    }

    @Scheduled(fixedRate = Constant.WebSocket.HEARTBEAT_INTERVAL)
    public void checkHeartbeats() {
        log.info("Checking heartbeats...");

        long currentTimeMillis = System.currentTimeMillis();

        onlineDevices.forEach((macAddress, deviceInfo) -> {
            if (!deviceInfo.isHeartbeatReceived()) {
                deviceInfo.incrementNoHeartbeatCount();
            } else {
                deviceInfo.resetNoHeartbeatCount();
            }

            if (deviceInfo.isOffline(currentTimeMillis)) {
                onlineDevices.remove(macAddress);

                log.info("Device " + macAddress + " is offline");

                broadcastOnlineDevices();
            }
            deviceInfo.setHeartbeatReceived(false);
        });
    }

    private void broadcastOnlineDevices() {
        messagingTemplate.convertAndSend(Constant.WebSocket.ONLINE_DEVICES, onlineDevices.values());
    }
}
