package co.wethinkcode.sentinel.security;

import co.wethinkcode.sentinel.model.User;
import co.wethinkcode.sentinel.repository.SecurityEventRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {

    @Test
    void adminShouldAccessAdminArea() {

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        AuthorizationService authorizationService =
                new AuthorizationService(securityEventRepository);

        User admin = new User(
                "admin",
                "password",
                "ADMIN"
        );

        boolean result =
                authorizationService.canAccessAdminArea(
                        admin,
                        "192.168.1.10"
                );

        assertTrue(result);
    }

    @Test
    void nurseShouldNotAccessAdminArea() {

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        AuthorizationService authorizationService =
                new AuthorizationService(securityEventRepository);

        User nurse = new User(
                "nurse",
                "password",
                "NURSE"
        );

        boolean result =
                authorizationService.canAccessAdminArea(
                        nurse,
                        "192.168.1.20"
                );

        assertFalse(result);
    }

    @Test
    void doctorShouldAccessMedicalArea() {

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        AuthorizationService authorizationService =
                new AuthorizationService(securityEventRepository);

        User doctor = new User(
                "doctor",
                "password",
                "DOCTOR"
        );

        boolean result =
                authorizationService.canAccessMedicalArea(doctor);

        assertTrue(result);
    }

    @Test
    void receptionistShouldAccessReceptionArea() {

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        AuthorizationService authorizationService =
                new AuthorizationService(securityEventRepository);

        User receptionist = new User(
                "receptionist",
                "password",
                "RECEPTIONIST"
        );

        boolean result =
                authorizationService.canAccessReceptionArea(receptionist);

        assertTrue(result);
    }

    @Test
    void blockedAdminShouldNotAccessAdminArea() {

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        AuthorizationService authorizationService =
                new AuthorizationService(securityEventRepository);

        User admin = new User(
                "admin",
                "password",
                "ADMIN",
                true
        );

        boolean result =
                authorizationService.canAccessAdminArea(
                        admin,
                        "192.168.1.10"
                );

        assertFalse(result);
    }

    @Test
void unauthorizedAccessShouldBeRecorded() {

    SecurityEventRepository securityEventRepository =
            new SecurityEventRepository();

    AuthorizationService authorizationService =
            new AuthorizationService(securityEventRepository);

    String username = "security_test_nurse";
    String ipAddress = "192.168.1.50";

    User nurse = new User(
            username,
            "password",
            "NURSE"
    );

    int eventsBefore =
            securityEventRepository.countUnauthorizedAccess(username);

    boolean result =
            authorizationService.canAccessAdminArea(
                    nurse,
                    ipAddress
            );

    int eventsAfter =
            securityEventRepository.countUnauthorizedAccess(username);

    assertFalse(result);
    assertEquals(eventsBefore + 1, eventsAfter);
}
}