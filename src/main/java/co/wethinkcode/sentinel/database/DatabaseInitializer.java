package co.wethinkcode.sentinel.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void main(String[] args) {
        createTables();
    }

    public static void createTables() {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
                """;

        String securityEventsTable = """
                CREATE TABLE IF NOT EXISTS security_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT,
                    event_type TEXT NOT NULL,
                    ip_address TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    severity TEXT NOT NULL
                )
                """;

        try (Connection connection = Database.connect();
             Statement statement = connection.createStatement()) {

            statement.execute(usersTable);
            statement.execute(securityEventsTable);

            System.out.println("Database tables created successfully!");

        } catch (Exception e) {
            System.out.println("Failed to create database tables.");
            e.printStackTrace();
        }
    }
}