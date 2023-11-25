package com.trung.springredisapp.config;

import com.trung.springredisapp.redis.RedisMessageSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Objects;

@Configuration
public class RedisConfig {
    @Autowired
    CustomRedisApllicationProperties redisApplicationProperties;

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

    // only has 1 connection a time in application
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // endpointUrl = System.getenv("REDIS_ENDPOINT_URL");
        // if(endpointUrl == null)
        //     endpointUrl = "127.0.0.1:6379";


        // String[] urlParts = endpointUrl.split(":");

        // if(urlParts.length > 1) {
        //     host = urlParts[0];
        //     port = urlParts[1];
        // }

        // System.out.println(host + "|" + port);
        RedisStandaloneConfiguration configuration = redisConfiguration();

        // password = System.getenv("REDIS_PASSWORD");
        // if( Objects.nonNull(password) )
        //     config.setPassword(password);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    RedisStandaloneConfiguration redisConfiguration() {
        String host = redisApplicationProperties.getHost();
        int port = redisApplicationProperties.getPort();

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);

        String password = redisApplicationProperties.getPassword();

        if (password.length() > 0) {
            configuration.setPassword(password);
        }

        return configuration;
    }
}