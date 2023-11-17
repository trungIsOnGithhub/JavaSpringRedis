package com.trung.springredisapp.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@Service
public class RedisMessageSubscriber implements MessageListener {
    CopyOnWriteArrayList<Function<String, Integer>> handlers = new CopyOnWriteArrayList<>();

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        String messageBody = new String(message.getBody());

        for (Function<String, Integer> handler : handlers) {
            handler.apply(messageBody);
        }
    }

    public void detach(Function<String, Integer> handler) {
        handlers.removeIf( e -> e.equals(handler) );
    }
    public void attach(Function<String, Integer> handler) {
        handlers.add(handler);
    }
}