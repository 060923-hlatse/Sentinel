package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.User;
import co.wethinkcode.sentinel.repository.SecurityEventRepository;
import co.wethinkcode.sentinel.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    @Test
    void shouldAllowCorrectLogin() {

        UserRepository userRepository = new UserRepository();
        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        userRepository.deleteByUsername("dr_smith");

        User user = new User(
                "dr_smith",
                "test123",
                "DOCTOR"
        );

        userRepository.save(user);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        securityEventRepository
                );

        boolean result =
                authenticationService.login(
                        "dr_smith",
                        "test123",
                        "192.168.1.20"
                );

        assertTrue(result);
    }

    @Test
    void shouldRejectWrongPassword() {

        UserRepository userRepository = new UserRepository();
        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        userRepository.deleteByUsername("dr_smith");

        User user = new User(
                "dr_smith",
                "test123",
                "DOCTOR"
        );

        userRepository.save(user);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        securityEventRepository
                );

        boolean result =
                authenticationService.login(
                        "dr_smith",
                        "wrongpassword",
                        "192.168.1.20"
                );

        assertFalse(result);
    }

    @Test
    void shouldBlockAccountAfterFiveFailedAttempts() {

        UserRepository userRepository = new UserRepository();

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        String username = "attacker_test";
        String ipAddress = "10.0.0.50";

        securityEventRepository.deleteFailedLogins(
                username,
                ipAddress
        );

        userRepository.deleteByUsername(username);

        User user = new User(
                username,
                "correctpassword",
                "DOCTOR"
        );

        userRepository.save(user);

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        securityEventRepository
                );

        // Simulate five failed login attempts
        for (int i = 0; i < 5; i++) {

            boolean result =
                    authenticationService.login(
                            username,
                            "wrongpassword",
                            ipAddress
                    );

            assertFalse(result);
        }

        // Retrieve the user from the database
        User blockedUser =
                userRepository.findByUsername(username);

        // Verify that the account is blocked
        assertNotNull(blockedUser);
        assertTrue(blockedUser.isBlocked());

        // Even the correct password should now be rejected
        boolean blockedLogin =
                authenticationService.login(
                        username,
                        "correctpassword",
                        ipAddress
                );

        assertFalse(blockedLogin);
    }
}

