package com.trung.springredisapp.config;

import com.trung.springredisapp.service.RedisMessageSubscriber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


@Configuration
public class RedisConfig {
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    @Primary
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber());
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory,
                                                 MessageListenerAdapter messageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener, topic());
        return container;
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("MESSAGES");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String host = null, port = null
                endpointUrl = null, password = null;

        String endpointUrl = System.getenv("REDIS_ENDPOINT_URL");
        String password = System.getenv("REDIS_PASSWORD");

        String[] urlParts = endpointUrl.split(":");

        if(urlParts.length > 1) {
            host = urlParts[0];
            port = urlParts[1];
        }

        this.fallbackIfNoValue(endpointUrl, password, host, port);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, Integer.parseInt(port));

        config.setPassword(password);

        return new LettuceConnectionFactory(config);
    }

    private void fallbackIfNoValue(String endpointUrl, String password, String host, String port) {
        if(endpointUrl == null) {
            endpointUrl = "127.0.0.1:6379";
        }
        if(password == null) {
            endpointUrl = "1234";
        }
        if(host == null) {
            host = "127.0.0.1";
        }
        if(port == null) {
            port = "6379";
        }
    }
}