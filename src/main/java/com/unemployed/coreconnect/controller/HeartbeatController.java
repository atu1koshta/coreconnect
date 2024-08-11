package com.unemployed.coreconnect.controller;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.dto.DeviceInfo;
import com.unemployed.coreconnect.service.HeartbeatService;

@Controller
public class HeartbeatController {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatController.class);

    @Autowired
    private HeartbeatService heartbeatService;

    @MessageMapping(Constant.WebSocket.HEARTBEAT)
    public void receiveHeartbeat(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        Integer id;
        String ipAddress;
        String macAddress;
        String deviceName;
        if (sessionAttributes != null) {
            id = (Integer) sessionAttributes.get("id");
            ipAddress = sessionAttributes.get("ipAddress").toString();
            macAddress = sessionAttributes.get("macAddress").toString();
            deviceName = sessionAttributes.get("deviceName").toString();

            log.info("Recieved heartbeat from " + ipAddress);

            heartbeatService.updateDevicesState(id, ipAddress, macAddress, deviceName);
        }
    }

    // To send initial list of online device as immediately after user logged in
    @SubscribeMapping(Constant.WebSocket.INITIAL_ONLINE_DEVICES)
    public Collection<DeviceInfo> initialOnlineDevices() {
        return heartbeatService.getOnlineDevices();
    }
}
