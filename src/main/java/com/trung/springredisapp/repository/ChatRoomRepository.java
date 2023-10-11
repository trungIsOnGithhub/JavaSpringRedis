package com.trung.springredisapp.repository;

import com.google.gson.Gson;
import com.trung.springredisapp.model.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class ChatRoomRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // message is stored in zset by date
    // room is stored in set
    public static final String USER_ROOMS_KEY = "user:%d:rooms";
    public static final String ROOM_NAME_KEY = "room:%s:name";
    public static final String ROOM_KEY = "room:%s";

    public Set<String> getUserRoomIds(int userId){
        Set<String> roomIds = redisTemplate.opsForSet().members(
            String.format(USER_ROOMS_KEY, userId)
        );
        return roomIds;
    }

    public boolean isRoomExists(String roomId){
        return redisTemplate.hasKey( String.format(ROOM_KEY, roomId) );
    }

    public String getRoomNameById(String roomId){
        return redisTemplate.opsForValue().get(
            String.format(ROOM_NAME_KEY, roomId)
        );
    }

    public Set<String> getMessages(String roomId, int offset, int size){
        Set<String> messages = redisTemplate.opsForZSet().reverseRange(
            String.format(ROOM_KEY, roomId),
            offset, offset + size
        );
        return messages;
    }

    public void sendMessageToRedis(String topic, String serializedMessage){
        redisTemplate.convertAndSend(topic, serializedMessage);
    }

    public void saveMessage(Message message){
        Gson gson = new Gson();
        redisTemplate.opsForZSet().add(
            String.format(ROOM_KEY, message.getRoomId()),
            gson.toJson(message), message.getDate()
        );
    }
}
