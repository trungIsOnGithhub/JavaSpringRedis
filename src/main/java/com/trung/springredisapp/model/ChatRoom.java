package com.trung.springredisapp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ChatRoom {
    private String id;
    private String[] names;

    public ChatRoom(String id, String name) {
        this.id = id;
        this.names = new String[1];

        this.names[0] = name;
    }

    public ChatRoom(String id, String name1, String name2) {
        this.id = id;
        this.names = new String[2];

        this.names[0] = name1;
        this.names[1] = name2;
    }

    public ChatRoom(String id, List<String> names) {
        this.id = id;
        this.names = new String[names.size()];

        for(int i=0; i<names.size(); ++i) {
            this.names[i] = names.get(i);
        }
    }
}