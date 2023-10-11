package com.trung.springredisapp.controller;

import com.google.gson.Gson;
import com.trung.springredisapp.model.Message;
import com.trung.springredisapp.model.ChatRoom;
import com.trung.springredisapp.model.User;
import com.trung.springredisapp.repository.ChatRoomRepository;
import com.trung.springredisapp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Objects;

@RestController
@RequestMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ChatRoomRepository roomsRepository;

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<ChatRoom>> getRooms(@PathVariable("userId") int userId) {
        Set<String> roomIds = roomsRepository.getUserRoomIds(userId);
        List<ChatRoom> rooms = new ArrayList<>();

        if (roomIds == null) {
            return new ResponseEntity<List<ChatRoom>>(HttpStatus.BAD_REQUEST);
        }

        for (String roomId : roomIds) {
            if( roomsRepository.isRoomExists(roomId) ) {
                String roomName = roomsRepository.getRoomNameById(roomId);

                ChatRoom chatRoom = createChatRoom(roomId, roomName);

                if( Objects.isNull(chatRoom) ) {
                    return new ResponseEntity<List<ChatRoom>>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                
                rooms.add(chatRoom);
            }
        }
        return new ResponseEntity<List<ChatRoom>>(rooms, HttpStatus.OK);
    }


    @GetMapping(value = "/{roomId}")
    public ResponseEntity<List<Message>> getRoomMessages(@PathVariable("roomId") String roomId,
                                                            @RequestParam("offset") Integer offset,
                                                            @RequestParam("size") Integer size ) {
        List<Message> messages = new ArrayList<>();

        if( roomsRepository.isRoomExists(roomId) ) {
            Set<String> values = roomsRepository.getMessages(roomId, offset, size);

            for (String value : values) {
                messages.add( messageDeserialize(value) );
            }
        }

        return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
    }

    private String[] getAllUserIds(String roomId){
        if( roomId.indexOf(':') < 1 ) {
            throw new RuntimeException("Canot get user id from wrong roomId input!!");
        }

        String[] allUserIds = roomId.split(":");

        if( allUserIds.length < 2 ) {
            throw new RuntimeException("Canot get user id from wrong roomId input!!");
        }

        return allUserIds;
    }

    private ChatRoom createChatRoom(String roomId, String roomName) {
        if( Objects.nonNull(roomName) ) {
            return new ChatRoom(roomId, roomName);
        }

        String[] allUserIds = getAllUserIds(roomId);

        List<String> allUserNames = new ArrayList<String>();

        for(String userId : allUserIds) {
            User userFromId = usersRepository.getUserById(Integer.parseInt(userId));

            if ( Objects.isNull(userFromId) ) { // inexist user
                return null;
            }

            allUserNames.add( userFromId.getUsername() );
        }
        return new ChatRoom(roomId, allUserNames); // temporary just 2 people
    }

    private Message messageDeserialize(String value){
        Gson gson = new Gson();
        try {
            return gson.fromJson(value, Message.class);
        } catch (Exception e) {
            System.out.println( String.format("Couldn't deserialize json: %s", value) );
        }
        return null;
    }
}