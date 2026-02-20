package ru.mock.storage;

import ru.mock.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    public static Map<String, User> users = new ConcurrentHashMap<>();
    public static Map<String, String> tokens = new ConcurrentHashMap<>();

    public static String generateToken(String login) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, login);
        return token;
    }
}
