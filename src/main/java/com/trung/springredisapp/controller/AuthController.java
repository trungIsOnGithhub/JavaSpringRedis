package com.trung.springredisapp.controller;

import com.google.gson.Gson;
import com.trung.springredisapp.config.SessionConfig;
import com.trung.springredisapp.payload.LoginDTO;
import com.trung.springredisapp.model.User;
import com.trung.springredisapp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

import java.util.Objects;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    @Autowired
    private UsersRepository usersRepository;

    @PostMapping(value = "/login")
    public ResponseEntity<User> login(@RequestBody LoginDTO loginDto, HttpSession session) {
        String username = loginDto.getUsername();

        if( !usersRepository.isUserExists(username) ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = usersRepository.getUserByName(username);

        if ( Objects.isNull(user) ){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }

        Gson gson = new Gson();
        session.setAttribute(SessionConfig.CHAT_USER_NAME, gson.toJson(user));

        user.setOnline(true);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PostMapping(value = "/logout")
    public ResponseEntity<Object> logout(Model model, HttpSession session) {
        Object user = session.getAttribute(SessionConfig.CHAT_USER_NAME);

        session.removeAttribute(SessionConfig.CHAT_USER_NAME);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}