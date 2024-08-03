package com.unemployed.coreconnect.model.dto;

public class PrivateMessage {
    private final String recipient;
    private final String message;

    public PrivateMessage(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public String getMessage() {
        return this.message;
    }
}
