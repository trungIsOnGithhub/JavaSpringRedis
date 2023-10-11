package com.trung.springredisapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int id;
    private String username;
    private boolean online;

    public User(int id, String username, boolean isOnline) {
        this.id = id;
        this.username = username;
        this.online = isOnline;
    }

    public int getId() {
        return this.id;
    }
}
