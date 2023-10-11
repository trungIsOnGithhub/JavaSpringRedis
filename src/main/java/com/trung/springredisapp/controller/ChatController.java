package com.trung.springredisapp.controller;

import com.google.gson.Gson;
import com.trung.springredisapp.model.*;
import com.trung.springredisapp.repository.RoomsRepository;
import com.trung.springredisapp.repository.UsersRepository;
import com.trung.springredisapp.service.RedisMessageSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;


@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoomsRepository roomsRepository;

    @Autowired
    ChannelTopic topic;

    @Autowired
    MessageListenerAdapter messageListener;

    static Function<String, Integer> streamEndpointHandler = (String message) -> {
        SseEmitter.SseEventBuilder event = SseEmitter.event().data(message);

        try {
            emitter.send(event);
        } catch (IOException e) {
            // This may occur when the client was disconnected.
            return 1;
        }
        return 0;
    };

    @RequestMapping("/stream")
    public SseEmitter streamSseMvc(@RequestParam int userId) {
        AtomicBoolean isComplete = new AtomicBoolean(false);
        SseEmitter emitter = new SseEmitter();

        RedisMessageSubscriber redisMessageSubscriber = (RedisMessageSubscriber) messageListener.getDelegate()
                                redisMessageSubscriber.attach(streamEndpointHandler);

        Runnable onDetach = () -> {
            redisMessageSubscriber.detach(streamEndpointHandler);
            if (!isComplete.get()) {
                isComplete.set(true);
                emitter.complete();
            }
        };

        emitter.onTimeout(onDetach);
        emitter.onCompletion(onDetach);
        emitter.onError( (error) -> onDetach.run() );

        return emitter;
    }


    @RequestMapping(value = "/emit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get(@RequestBody ChatControllerMessage chatMessage) {
        Gson gson = new Gson();
        String serializedMessage;

        if(chatMessage.getType() == MessageType.MESSAGE) {
            serializedMessage = handleRegularMessageCase(chatMessage);
        }
        else if(chatMessage.getType() == MessageType.USER_CONNECTED || chatMessage.getType() == MessageType.USER_DISCONNECTED) {
            serializedMessage = handleUserConnectionCase(chatMessage);
        }
        else {
            serializedMessage = gson.toJson(new PubSubMessage<>(chatMessage.getType().value(), chatMessage.getData()));
        }

        roomsRepository.sendMessageToRedis(topic.getTopic(), serializedMessage);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private String handleRegularMessageCase(ChatControllerMessage chatMessage){
        Gson gson = new Gson();
        Message message = gson.fromJson(chatMessage.getData(), Message.class);

        usersRepository.addUserToOnlineList(message.getFrom());
        //redisTemplate.opsForSet().add(ONLINE_USERS_KEY, message.getFrom());
        roomsRepository.saveMessage(message);

        return gson.toJson(new PubSubMessage<>(chatMessage.getType().value(), message));
    }

    private String handleUserConnectionCase(ChatControllerMessage chatMessage){
        Gson gson = new Gson();

        int userId = chatMessage.getUser().getId();
        String messageType = chatMessage.getType().value();

        User serializedUser = gson.fromJson(chatMessage.getData(), User.class);

        String serializedMessage = gson.toJson(new PubSubMessage<>(messageType, serializedUser));

        if(chatMessage.getType() == MessageType.USER_CONNECTED) {
            usersRepository.addUserToOnlineList(String.valueOf(userId));
        }
        else {
            usersRepository.removeUserFromOnlineList(String.valueOf(userId));
        }
        return serializedMessage;
    }
}
