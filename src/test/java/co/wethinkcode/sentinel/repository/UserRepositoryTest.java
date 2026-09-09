package co.wethinkcode.sentinel.repository;

import co.wethinkcode.sentinel.model.User;

public class UserRepositoryTest {

    public static void main(String[] args) {

        User user = new User(
                "dr_smith",
                "test123",
                "DOCTOR"
        );

        UserRepository repository = new UserRepository();

        repository.save(user);

        User foundUser = repository.findByUsername("dr_smith");

        if (foundUser != null) {
            System.out.println("User found!");
            System.out.println("Username: " + foundUser.getUsername());
            System.out.println("Role: " + foundUser.getRole());
        } else {
            System.out.println("User not found.");
        }
    }
}
