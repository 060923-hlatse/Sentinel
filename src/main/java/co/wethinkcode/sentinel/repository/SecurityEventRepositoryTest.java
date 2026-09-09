package co.wethinkcode.sentinel.repository;

import co.wethinkcode.sentinel.model.SecurityEvent;

public class SecurityEventRepositoryTest {

    public static void main(String[] args) {

        SecurityEvent event = new SecurityEvent(
                "dr_smith",
                "LOGIN_FAILED",
                "192.168.1.20",
                "HIGH"
        );

        SecurityEventRepository repository =
                new SecurityEventRepository();

        repository.save(event);
    }

    
}
