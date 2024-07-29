package com.unemployed.coreconnect.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerResponseMessage {
	private String sender;
	private String message;
	private String time;

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
