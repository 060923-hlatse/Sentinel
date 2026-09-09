package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.SecurityEvent;
import co.wethinkcode.sentinel.model.User;
import co.wethinkcode.sentinel.repository.SecurityEventRepository;

public class AuthorizationService {

    private final SecurityEventRepository securityEventRepository;

    public AuthorizationService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public boolean canAccessAdminArea(User user, String ipAddress) {

        if (user == null || user.isBlocked()) {
            return false;
        }

        if (user.getRole().equals("ADMIN")) {
            return true;
        }

        recordUnauthorizedAccess(user, ipAddress);

        return false;
    }

    public boolean canAccessMedicalArea(User user) {

        if (user == null || user.isBlocked()) {
            return false;
        }

        return user.getRole().equals("ADMIN")
                || user.getRole().equals("DOCTOR")
                || user.getRole().equals("NURSE");
    }

    public boolean canAccessReceptionArea(User user) {

        if (user == null || user.isBlocked()) {
            return false;
        }

        return user.getRole().equals("ADMIN")
                || user.getRole().equals("RECEPTIONIST");
    }

    private void recordUnauthorizedAccess(User user, String ipAddress) {

        SecurityEvent event = new SecurityEvent(
                user.getUsername(),
                "UNAUTHORIZED_ACCESS",
                ipAddress,
                "HIGH"
        );

        securityEventRepository.save(event);

        System.out.println(
                "🚨 UNAUTHORIZED ACCESS ATTEMPT: "
                        + user.getUsername()
        );
    }
}
