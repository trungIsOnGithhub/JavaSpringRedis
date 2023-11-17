package com.trung.springredisapp.model;

public class PubSubMessage<T> {
    private T data;
    private String type;

    public T getData() {
        return this.data;
    }
    public String getType() {
        return this.type;
    }

    public PubSubMessage(String type, T data) {
        this.type = type;
        this.data = data;
    }
}