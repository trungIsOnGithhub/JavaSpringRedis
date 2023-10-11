package com.trung.springredisapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter
public class Message {
    @NonNull
    private String from;
    @NonNull
    private int date;
    @NonNull
    private String message;
    @NonNull
    private String roomId;
}