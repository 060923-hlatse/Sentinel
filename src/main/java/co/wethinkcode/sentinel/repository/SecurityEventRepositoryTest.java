package co.wethinkcode.sentinel.repository;

import co.wethinkcode.sentinel.model.SecurityEvent;

import java.util.List;

public class SecurityEventRepositoryTest {

    public static void main(String[] args) {

        SecurityEventRepository repository =
                new SecurityEventRepository();

        List<SecurityEvent> events =
                repository.findRecentEvents();

        System.out.println("Recent security events: " + events.size());

        for (SecurityEvent event : events) {

            System.out.println(
                    event.getUsername()
                            + " | "
                            + event.getEventType()
                            + " | "
                            + event.getSeverity()
            );
        }
    }
}
