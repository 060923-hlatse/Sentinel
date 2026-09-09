package co.wethinkcode.sentinel.database;

import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        try (Connection connection = Database.connect()) {
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }
}
