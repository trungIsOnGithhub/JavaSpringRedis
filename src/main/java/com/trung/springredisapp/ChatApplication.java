package com.trung.springredisapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ChatApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", 8080));
        app.run(args);
    }
}