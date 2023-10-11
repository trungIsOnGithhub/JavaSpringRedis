package com.trung.springredisapp.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

@Getter
@Setter
public class ChatControllerMessage {
    @NonNull
    private MessageType type;
    @NonNull
    private User user;
    @NonNull
    private String data;
}