package com.trung.springredisapp.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomRedisApllicationProperties {
    // SPeL - Reference: https://www.baeldung.com/spring-value-annotation
    // Example propeties file: redisValuesMap={host: 'localhost', port: '6379', password: ''}
    @Value("#{${redisValuesMap}}")
    private Map<String, String> redisValueMap;

    public String getHost() {
        if (!redisValueMap.containsKey("host")) {
            return DEFAULT_REDIS_HOST;
        }
        return this.redisValueMap.get("host");
    }

    public int getPort() {
        if (!redisValueMap.containsKey("port") ) {
            return DEFAULT_REDIS_PORT;
        }
        
        String portStrValue = this.redisValueMap.get("port");

        if (!isInteger(portStrValue)) {
            return DEFAULT_REDIS_PORT;
        }

        return Integer.parseInt(portStrValue);
    }

    public String getPassword() {
        if (!redisValueMap.containsKey("password")) {
            return DEFAULT_REDIS_PASSWORD;
        }
        return this.redisValueMap.get("password");
    }

    private static String DEFAULT_REDIS_HOST = "127.0.0.1";
    private static int DEFAULT_REDIS_PORT = 6379;
    private static String DEFAULT_REDIS_PASSWORD = "";

    private static final boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch(Exception exception) {
            return false;
        }

        return true;
    }
}
