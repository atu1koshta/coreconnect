package com.unemployed.coreconnect.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.Message;
import com.unemployed.coreconnect.model.ServerResponseMessage;

@Controller
public class ChatController {
	@SuppressWarnings("null")
	@MessageMapping(Constant.WebSocket.CHAT)
	@SendTo(Constant.WebSocket.MESSAGES)
	public ServerResponseMessage send(Message message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		String deviceName = headerAccessor.getSessionAttributes().get("deviceName").toString();
		System.out.println("Received message: " + message.getContent() + " from " + deviceName);
		return new ServerResponseMessage(deviceName, message.getContent());
	}
}
