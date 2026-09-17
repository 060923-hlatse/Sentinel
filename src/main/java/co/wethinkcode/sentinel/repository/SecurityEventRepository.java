
package co.wethinkcode.sentinel.repository;

import co.wethinkcode.sentinel.database.Database;
import co.wethinkcode.sentinel.model.SecurityEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SecurityEventRepository {

    public void save(SecurityEvent event) {

        String sql = """
                INSERT INTO security_events
                (username, event_type, ip_address, severity)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getUsername());
            statement.setString(2, event.getEventType());
            statement.setString(3, event.getIpAddress());
            statement.setString(4, event.getSeverity());

            statement.executeUpdate();

            System.out.println("Security event saved successfully!");

        } catch (Exception e) {
            System.out.println("Failed to save security event.");
            e.printStackTrace();
        }
    }

    public int countRecentFailedLogins(String username, String ipAddress) {

        String sql = """
                SELECT COUNT(*)
                FROM security_events
                WHERE username = ?
                AND ip_address = ?
                AND event_type = 'LOGIN_FAILED'
                AND timestamp >= datetime('now', '-5 minutes')
                """;

        try (Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, ipAddress);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void deleteFailedLogins(String username, String ipAddress) {

        String sql = """
                DELETE FROM security_events
                WHERE username = ?
                AND ip_address = ?
                AND event_type = 'LOGIN_FAILED'
                """;

        try (Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, ipAddress);

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countUnauthorizedAccess(String username) {

        String sql = """
                SELECT COUNT(*)
                FROM security_events
                WHERE username = ?
                AND event_type = 'UNAUTHORIZED_ACCESS'
                """;

        try (Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<SecurityEvent> findRecentEvents() {

        List<SecurityEvent> events = new ArrayList<>();

        String sql = """
                SELECT username, event_type, ip_address, severity
                FROM security_events
                ORDER BY timestamp DESC
                LIMIT 20
                """;

        try (Connection connection = Database.connect();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                events.add(
                        new SecurityEvent(
                                result.getString("username"),
                                result.getString("event_type"),
                                result.getString("ip_address"),
                                result.getString("severity")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    public List<SecurityEvent> findHighSeverityEvents() {

        List<SecurityEvent> events = new ArrayList<>();

        String sql = """
                SELECT username, event_type, ip_address, severity
                FROM security_events
                WHERE severity = 'HIGH'
                ORDER BY timestamp DESC
                LIMIT 20
                """;

        try (Connection connection = Database.connect();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                events.add(
                        new SecurityEvent(
                                result.getString("username"),
                                result.getString("event_type"),
                                result.getString("ip_address"),
                                result.getString("severity")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

}


