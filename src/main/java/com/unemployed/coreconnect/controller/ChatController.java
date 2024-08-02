package com.unemployed.coreconnect.controller;

import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.ServerResponseMessage;
import com.unemployed.coreconnect.utils.Logging;

@Controller
public class ChatController implements Logging {

    private final Logger log = getLogger();

    @SuppressWarnings("null")
    @MessageMapping(Constant.WebSocket.CHAT)
    @SendTo(Constant.WebSocket.MESSAGES)
    public ServerResponseMessage send(String message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String deviceName = headerAccessor.getSessionAttributes().get("deviceName").toString();

        return new ServerResponseMessage(deviceName, message);
    }
}
