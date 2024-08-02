package com.unemployed.coreconnect.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.PrivateMessage;
import com.unemployed.coreconnect.model.ServerResponseMessage;
import com.unemployed.coreconnect.utils.Logging;

@Controller
public class ChatController implements Logging {

    private final Logger log = getLogger();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping(Constant.WebSocket.CHAT)
    @SendTo(Constant.WebSocket.MESSAGES)
    public ServerResponseMessage send(String message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String deviceName = getDeviceNameFromHeader(headerAccessor);

        if (deviceName == null) {
            return null;
        }

        return new ServerResponseMessage(deviceName, message);
    }

    @MessageMapping(Constant.WebSocket.PRIVATE_CHAT)
    public void handlePrivateMessage(PrivateMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String deviceName = getDeviceNameFromHeader(headerAccessor);

        if (deviceName == null) {
            return;
        }

        log.info(String.format("From: %s, To: %s, Message: %s", deviceName, message.getRecipient(), message.getMessage()));

        try {
            /*
            * TODO: Send message to recipient's private queue
            * Set 'username' as first param of convertAndSendToUser to correctly identify recipient's session
            */ 
            
            String payload = objectMapper.writeValueAsString(Map.of("sender", deviceName, "message", message.getMessage()));
            messagingTemplate.convertAndSendToUser("user", "/queue/messages", payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert message to JSON", e);
        }
    }

    private String getDeviceNameFromHeader(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes == null) {
            log.warn("Cannot find logged in device in record.");
            return null;
        }

        return attributes.get("deviceName").toString();
    }
}
