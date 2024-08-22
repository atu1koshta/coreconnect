package com.unemployed.coreconnect.model.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerResponseMessage {
	private final String sender;
	private final String message;
	private final String time;

	public ServerResponseMessage(String sender, String content) {
		this.sender = sender;
		this.message = content;
		this.time = new SimpleDateFormat("HH:mm").format(new Date());
	}

	public String getMessage() {
		return message;
	}

	public String getTime() {
		return time;
	}

	public String getSender() {
		return sender;
	}
}
