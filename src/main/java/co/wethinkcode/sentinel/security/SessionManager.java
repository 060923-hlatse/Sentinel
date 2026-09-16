package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, User> sessions =
            new ConcurrentHashMap<>();

}
