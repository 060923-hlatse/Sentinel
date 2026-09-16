package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, User> sessions =
            new ConcurrentHashMap<>();

    public String createSession(User user) {

        String sessionId = UUID.randomUUID().toString();

        sessions.put(sessionId, user);

        return sessionId;
    }

    public User getUser(String sessionId) {
        return sessions.get(sessionId);
    }
}