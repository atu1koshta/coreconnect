package com.unemployed.coreconnect.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import com.unemployed.coreconnect.model.Message;
import com.unemployed.coreconnect.model.ServerResponseMessage;

@Controller
public class ChatController {
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public ServerResponseMessage send(Message message, WebSocketSession session) throws Exception {
		String deviceName = (String) session.getAttributes().get("deviceName");
		return new ServerResponseMessage(deviceName, message.getContent());
	}
}
