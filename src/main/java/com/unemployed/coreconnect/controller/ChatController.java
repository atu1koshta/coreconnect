package com.unemployed.coreconnect.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.unemployed.coreconnect.model.Message;
import com.unemployed.coreconnect.model.ServerResponseMessage;

@Controller
public class ChatController {
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public ServerResponseMessage send(Message message) throws Exception {
		return new ServerResponseMessage();
	}
}
