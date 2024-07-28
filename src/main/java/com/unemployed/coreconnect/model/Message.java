package com.unemployed.coreconnect.model;

public class Message {
	private String content;
	private String sender;

	public Message() {
	}

	public Message(String sender, String content) {
		this.sender = sender;
		this.content = content;

	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
