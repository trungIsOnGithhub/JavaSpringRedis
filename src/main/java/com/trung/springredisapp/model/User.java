package com.trung.springredisapp.model;

public class User {
    private int id;
    private String username;
    private boolean online;

    public String getUsername() {
        return this.username;
    }
    public int getId() {
        return this.id;
    }
    public boolean getIsOnline() {
        return this.online;
    }
    public void setOnline(boolean isOnline) {
        this.online = isOnline;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public User(int id, String username, boolean isOnline) {
        this.id = id;
        this.username = username;
        this.online = isOnline;
    }
}
