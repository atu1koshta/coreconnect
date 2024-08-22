package com.unemployed.coreconnect.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.dto.PrivateMessage;
import com.unemployed.coreconnect.model.dto.ServerResponseMessage;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping(Constant.WebSocket.CHAT)
    @SendTo(Constant.WebSocket.MESSAGES)
    public ServerResponseMessage send(String message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        if (attributes == null) {
            return null;
        }

        String username = (String) attributes.get("username").toString();

        return new ServerResponseMessage(username, message);
    }

    @MessageMapping(Constant.WebSocket.PRIVATE_CHAT)
    public void handlePrivateMessage(PrivateMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        if (attributes == null) {
            return;
        }

        String username = (String) attributes.get("username").toString();

        log.info(String.format("From: %s, To: %s, Message: %s", username, message.getRecipient(), message.getMessage()));

        try {
            String payload = objectMapper.writeValueAsString(Map.of("sender", username, "message", message.getMessage(), "time", new SimpleDateFormat("HH:mm").format(new Date())));
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", payload);
            messagingTemplate.convertAndSendToUser(message.getRecipient(), "/queue/messages", payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert message to JSON", e);
        }
    }
}
