package com.trung.springredisapp;

import com.trung.springredisapp.model.Message;
import com.trung.springredisapp.model.ChatRoom;
import com.trung.springredisapp.model.User;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class DemoDataGenerator {
    private StringRedisTemplate redisTemplate;

    @Autowired
    public DemoDataGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.createDemoData();
    }

    private static final String DEMO_PASSWORD = "password123";
    private static final List<String> DEMO_USERNAME_LIST = Arrays.asList("User1", "User2", "User3", "User4");
    private static final List<String> DEMO_GREETING_LIST = Arrays.asList("Hello", "Hi", "Yo", "Hola");
    private static final List<String> DEMO_MESSAGES_LIST = Arrays.asList("Hello", "Hi", "Yo", "Hola");

    private void createDemoData() {
        // We store a counter for the total users and increment it on each register
        final AtomicBoolean totalUsersKeyExist = new AtomicBoolean(redisTemplate.hasKey("total_users")); // Client.KeyExistsAsync("total_users");
        if (!totalUsersKeyExist.get()) {
            redisTemplate.opsForValue().set("total_users", "0");
            redisTemplate.opsForValue().set("room:0:name", "General");


            List<User> users = new LinkedList<>();
            for (String username : DEMO_USERNAME_LIST) {
                User user = createUser(username);
                users.add(user);
            }

            Map<String, ChatRoom> rooms = new HashMap<>();
            for (User user : users) {
                List<User> otherUsers = users.stream().filter(x -> x.getId() != user.getId()).collect(Collectors.toList());
                for (var otherUser : otherUsers) {
                    String privateRoomId = getPrivateRoomId(user.getId(), otherUser.getId());
                    ChatRoom room;
                    if (!rooms.containsKey(privateRoomId)) {
                        room = createPrivateRoom(user.getId(), otherUser.getId());
                        rooms.put(privateRoomId, room);
                    }
                    addMessage(privateRoomId, String.valueOf(otherUser.getId()), getRandomGreeting(), generateMessageDate());
                }
            }

            for (int messageIndex = 0; messageIndex < DEMO_MESSAGES_LIST.size(); messageIndex++) {
                int messageDate = getTimestamp() - ((DEMO_MESSAGES_LIST.size() - messageIndex) * 200);
                addMessage("0", getRandomUserId(users), DEMO_MESSAGES_LIST.get(messageIndex), messageDate)
                ;
            }
        }
    }

    private String getRandomGreeting(){
        return DEMO_GREETING_LIST.get((int) Math.floor(Math.random() * DEMO_GREETING_LIST.size()));
    }

    private int getTimestamp(){
        return Long.valueOf((System.currentTimeMillis() / 1000L)).intValue();
    }

    private void addMessage(String roomId, String fromId, String content, Integer timeStamp) {
        Gson gson = new Gson();
        String roomKey = String.format("room:%s", roomId);
        Message message = new Message(
                fromId,
                timeStamp,
                content,
                roomId
        );
        redisTemplate.opsForZSet().add(roomKey, gson.toJson(message), message.getDate());
    }

    private User createUser(String username){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String usernameKey = String.format("username:%s", username);

        // Yeah, bcrypt generally ins't used in .NET, this one is mainly added to be compatible with Node and Python demo servers.
        String hashedPassword = encoder.encode(DEMO_PASSWORD);

        Integer nextId = redisTemplate.opsForValue().increment("total_users").intValue();
        String userKey = String.format("user:%s", nextId);

        redisTemplate.opsForValue().set(usernameKey, userKey);
        redisTemplate.opsForHash().put(userKey, "username", username);
        redisTemplate.opsForHash().put(userKey, "password", hashedPassword);

        String roomsKey = String.format("user:%s:rooms", nextId);
        redisTemplate.opsForSet().add(roomsKey, "0");

        return new User(
                nextId,
                username,
                false
        );
    }

    private String getPrivateRoomId(Integer userId1, Integer userId2) {
        Integer minUserId = userId1 > userId2 ? userId2 : userId1;
        Integer maxUserId = userId1 > userId2 ? userId1 : userId2;
        return String.format("%d:%d", minUserId, maxUserId);
    };

    private ChatRoom createPrivateRoom(Integer user1, Integer user2){
        String roomId = getPrivateRoomId(user1, user2);

        String userRoomkey1 = String.format("user:%d:rooms", user1);
        String userRoomkey2 = String.format("user:%d:rooms", user2);

        redisTemplate.opsForSet().add(userRoomkey1, roomId);
        redisTemplate.opsForSet().add(userRoomkey2, roomId);

        String key1 = String.format("user:%d", user1);
        String key2 = String.format("user:%d", user2);

        return new ChatRoom(
                roomId,
                (String) redisTemplate.opsForHash().get(key1, "username"),
                (String) redisTemplate.opsForHash().get(key2, "username")
        );
    }

    private int generateMessageDate(){
        return (int) (getTimestamp() - Math.random() * 222);
    }

    private String getRandomUserId(List<User> users){
        return String.valueOf(users.get((int) Math.floor(users.size() * Math.random())).getId());
    }
}