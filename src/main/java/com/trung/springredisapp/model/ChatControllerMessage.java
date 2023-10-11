package com.trung.springredisapp.model;

import lombok.Getter;
import lombok.Setter;

import com.trung.springredisapp.payload.MessageType;

@Getter
@Setter
public class ChatControllerMessage {
    private MessageType type;
    private User user;
    private String data;

    public MessageType getType() {
        return this.type;
    }

    public String getData() {
        return this.data;
    }
}