package co.wethinkcode.sentinel;

import co.wethinkcode.sentinel.model.User;
import co.wethinkcode.sentinel.repository.UserRepository;

public class CreateAdmin {

    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository();

        User admin = new User(
                "admin_sarah",
                "admin123",
                "ADMIN"
        );

        userRepository.save(admin);

        System.out.println("Admin user created.");
    }
}
