package co.wethinkcode.sentinel.repository;

import co.wethinkcode.sentinel.database.Database;
import co.wethinkcode.sentinel.model.User;
import co.wethinkcode.sentinel.security.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {

    public void save(User user) {

        String sql = """
                INSERT INTO users (username, password, role, blocked)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());

            String hashedPassword =
                    PasswordHasher.hash(user.getPassword());

            statement.setString(2, hashedPassword);
            statement.setString(3, user.getRole());
            statement.setBoolean(4, user.isBlocked());

            statement.executeUpdate();

            System.out.println("User saved successfully!");

        } catch (Exception e) {
            System.out.println("Failed to save user.");
            e.printStackTrace();
        }
    }

    public User findByUsername(String username) {

        String sql = """
                SELECT username, password, role, blocked
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                return new User(
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("role"),
                        result.getBoolean("blocked")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteByUsername(String username) {

        String sql = """
                DELETE FROM users
                WHERE username = ?
                """;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            statement.executeUpdate();

            System.out.println("User deleted successfully!");

        } catch (Exception e) {
            System.out.println("Failed to delete user.");
            e.printStackTrace();
        }
    }

    public void blockUser(String username) {

        String sql = """
                UPDATE users
                SET blocked = 1
                WHERE username = ?
                """;

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            statement.executeUpdate();

            System.out.println("User account blocked!");

        } catch (Exception e) {
            System.out.println("Failed to block user.");
            e.printStackTrace();
        }
    }
}

