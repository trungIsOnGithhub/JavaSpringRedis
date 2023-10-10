package com.trung.springredisapp.repository;

import com.trung.springredisapp.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class UsersRepository {
    private static final String USER_ID_KEY = "user:%s";
    private static final String USERNAME_KEY = "username:%s";

    private static final String USERNAME_HASH_KEY = "username";
    private static final String ONLINE_USERS_KEY = "onluser";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public User getUserById(int userId) {
        String usernameKey = String.format(USER_ID_KEY, userId);
        String username = (String)redisTemplate.opsForHash().get(usernameKey, USERNAME_HASH_KEY);
    
        if (username == null)
            return null;
    
        boolean isOnline = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, String.valueOf(userId));
        return new User(userId, username, isOnline);
    }

    public Set<Integer> getOnlineUsersIds(){
        Set<String> onlineIds = redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
        if (onlineIds == null)
            return null;
        return onlineIds.stream().map(Integer::parseInt).collect(Collectors.toSet());
    }

    public User getUserByName(String username) {
        if (!isUserExists(username)) {
            return null;
        }

        String userKey = redisTemplate.opsForValue().get( String.format(USERNAME_KEY, username) );

        Objects.requireNonNull(userKey);

        boolean isOnline = isUserOnline(
            String.valueOf( parseUserId(userKey) )
        );
        // all the info needed to store the valid user object into session.
        return new User(userId, username, isOnline);
    }


    public boolean isUserExists(String username) {
        return redisTemplate.hasKey(String.format(USERNAME_KEY, username));
    }
    public boolean isUserOnline(String userId) {
        return redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, userId);
    }
    public void addUserToOnlineList(String userId){
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, userId);
    }

    public void removeUserFromOnlineList(String userId){
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, userId);
    }

    private int parseUserId(String userKey){
        String[] userIds = userKey.split(":");
        return Integer.parseInt(userIds[userIds.length - 1]);
    }
}