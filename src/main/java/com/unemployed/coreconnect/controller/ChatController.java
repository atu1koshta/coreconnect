package com.unemployed.coreconnect.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.ServerResponseMessage;

@Controller
public class ChatController {
	@SuppressWarnings("null")
	@MessageMapping(Constant.WebSocket.CHAT)
	@SendTo(Constant.WebSocket.MESSAGES)
	public ServerResponseMessage send(String message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		String deviceName = headerAccessor.getSessionAttributes().get("deviceName").toString();

		return new ServerResponseMessage(deviceName, message);
	}
}
