package com.trung.springredisapp.config;

public class RedisKeyScheme {
    private static final String USER_ROOMS_KEY = "user:%d:rooms";
    public static String getUserRoomsKey(int userId) {
        return String.format(USER_ROOMS_KEY, userId);
    }

    private static final String ROOM_KEY = "room:%s";
    public static String getRoomKey(String roomId) {
        return String.format(ROOM_KEY, roomId);
    }
    
    private static final String ROOM_NAME_KEY = "room:%s:name";
    public static String getRoomNameKey(String roomId) {
        return String.format(ROOM_NAME_KEY, roomId);
    }
}