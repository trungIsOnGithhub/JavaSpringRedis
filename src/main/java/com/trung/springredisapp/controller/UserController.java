package com.trung.springredisapp.controller;

import com.trung.springredisapp.config.SessionConfig;
import com.trung.springredisapp.model.User;
import com.trung.springredisapp.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import java.util.*;
import javax.servlet.http.HttpSession;
import java.util.stream.Collectors;
import java.util.Objects;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UsersRepository usersRepository;

    @RequestMapping
    public ResponseEntity<Map<String, User>> get(@RequestParam(value = "ids") String idsString) {
        Set<Integer> ids = parseIds(idsString);

        Map<String, User> usersMap = new HashMap<>();

        for (Integer id : ids) {
            User user = usersRepository.getUserById(id);
            if (user == null){
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
            }
            usersMap.put(String.valueOf(user.getId()), user);
        }

        return new ResponseEntity<>(usersMap, HttpStatus.OK);
    }
    private Set<Integer> parseIds(String idsString){
        return Arrays.stream(idsString.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }


    @RequestMapping(value = "/me")
    public ResponseEntity<User> getMe(Model model, HttpSession session) {
        String user = (String) session.getAttribute(SessionConfig.CHAT_USER_NAME);
        if (user == null){
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        Gson gson = new Gson();
        return new ResponseEntity<User>(gson.fromJson(user, User.class), HttpStatus.OK);
    }


    @RequestMapping(value = "/online")
    public ResponseEntity<Map<String, User>> getOnlineUsers() {
        Map<String, User> usersMap = new HashMap<>();

        Set<Integer> onlineIds = usersRepository.getOnlineUsersIds();
        if( Objects.isNull(onlineIds) ) {
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        }

        for(var onlineId : onlineIds) {
            User user = usersRepository.getUserById(onlineId);
            if (user == null){
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            usersMap.put(String.valueOf(user.getId()), user);
        }

        return new ResponseEntity<>(usersMap, HttpStatus.OK);
    }
}