package com.unemployed.coreconnect.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerResponseMessage {
	private String message;
	private String time;

	public ServerResponseMessage() {
		this.message = "Server: ACK at ";
		this.time = new SimpleDateFormat("HH:mm").format(new Date());
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
