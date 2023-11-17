package com.trung.springredisapp.model;

import com.trung.springredisapp.payload.MessageType;

public class ChatControllerMessage {
    private MessageType type;
    private User user;
    private String data;

    public MessageType getType() {
        return this.type;
    }
    public User getUser() {
        return this.user;
    }
    public String getData() {
        return this.data;
    }
}