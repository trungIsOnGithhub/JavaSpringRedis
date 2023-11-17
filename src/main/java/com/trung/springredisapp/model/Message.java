package com.trung.springredisapp.model;

public class Message {
    private String from;
    private int date;
    private String message;
    private String roomId;

    public String getRoomId() {
        return this.roomId;
    }
    public String getMessage() {
        return this.message;
    }
    public int getDate() {
        return this.date;
    }
    public String getFrom() {
        return this.from;
    }

    public Message(String from, int date, String message, String roomId) {
        this.from = from;
        this.date = date;
        this.message = message;
        this.roomId = roomId;
    }
}