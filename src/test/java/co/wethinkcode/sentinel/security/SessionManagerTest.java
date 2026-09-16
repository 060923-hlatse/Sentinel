package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @Test
    void shouldCreateAndRetrieveSession() {

        SessionManager sessionManager =
                new SessionManager();

        User user =
                new User(
                        "admin_sarah",
                        "admin123",
                        "ADMIN"
                );

        String sessionId =
                sessionManager.createSession(user);

        User retrievedUser =
                sessionManager.getUser(sessionId);

        assertNotNull(sessionId);
        assertEquals("admin_sarah", retrievedUser.getUsername());
        assertEquals("ADMIN", retrievedUser.getRole());
    }

    @Test
    void shouldRemoveSession() {

        SessionManager sessionManager =
                new SessionManager();

        User user =
                new User(
                        "admin_sarah",
                        "admin123",
                        "ADMIN"
                );

        String sessionId =
                sessionManager.createSession(user);

        sessionManager.removeSession(sessionId);

        User retrievedUser =
                sessionManager.getUser(sessionId);

        assertNull(retrievedUser);
    }
}
