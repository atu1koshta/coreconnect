package com.unemployed.coreconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.service.HeartbeatService;


@Controller
public class HeartbeatController {
    @Autowired
    private HeartbeatService heartbeatService;

	@MessageMapping(Constant.WebSocket.HEARTBEAT)
    public void receiveHeartbeat(SimpMessageHeaderAccessor headerAccessor) {
    	System.out.println("Received heartbeat...");
    	heartbeatService.updateDevicesState(headerAccessor);
    }
}
