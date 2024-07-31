package com.unemployed.coreconnect.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.service.HeartbeatService;
import com.utils.Logging;

@Controller
public class HeartbeatController implements Logging {

    private final Logger log = getLogger();

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
}
