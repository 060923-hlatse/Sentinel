package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.SecurityEvent;
import co.wethinkcode.sentinel.model.User;
import co.wethinkcode.sentinel.repository.SecurityEventRepository;
import co.wethinkcode.sentinel.repository.UserRepository;

public class AuthenticationService {

    private final UserRepository userRepository;
    private final SecurityEventRepository securityEventRepository;

    public AuthenticationService(UserRepository userRepository,
                                 SecurityEventRepository securityEventRepository) {

        this.userRepository = userRepository;
        this.securityEventRepository = securityEventRepository;
    }

    public boolean login(String username, String password, String ipAddress) {

        User user = userRepository.findByUsername(username);

        // User does not exist
        if (user == null) {

            recordFailedLogin(username, ipAddress);

            return false;
        }

        // Account has already been blocked
        if (user.isBlocked()) {

            SecurityEvent event = new SecurityEvent(
                    username,
                    "BLOCKED_LOGIN_ATTEMPT",
                    ipAddress,
                    "HIGH"
            );

            securityEventRepository.save(event);

            System.out.println(
                    "🔒 BLOCKED LOGIN ATTEMPT: " + username
            );

            return false;
        }

        // Check password
        boolean passwordCorrect =
                PasswordHasher.verify(
                        password,
                        user.getPassword()
                );

        if (!passwordCorrect) {

            recordFailedLogin(username, ipAddress);

            return false;
        }

        // Successful login
        return true;
    }

    private void recordFailedLogin(String username, String ipAddress) {

        SecurityEvent event = new SecurityEvent(
                username,
                "LOGIN_FAILED",
                ipAddress,
                "LOW"
        );

        securityEventRepository.save(event);

        int failedAttempts =
                securityEventRepository.countRecentFailedLogins(
                        username,
                        ipAddress
                );

        System.out.println(
                "Failed login attempts: " + failedAttempts
        );

        if (failedAttempts >= 5) {

            SecurityEvent bruteForceEvent = new SecurityEvent(
                    username,
                    "BRUTE_FORCE_DETECTED",
                    ipAddress,
                    "HIGH"
            );

            securityEventRepository.save(bruteForceEvent);

            // Block the account
            userRepository.blockUser(username);

            System.out.println(
                    "🚨 BRUTE-FORCE ATTACK DETECTED!"
            );

            System.out.println(
                    "🔒 ACCOUNT BLOCKED: " + username
            );
        }
    }
}

