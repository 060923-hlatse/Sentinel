package co.wethinkcode.sentinel.security;

public class PasswordHasherTest {

    public static void main(String[] args) {

        String password = "test123";

        String hashedPassword = PasswordHasher.hash(password);

        System.out.println("Original password: " + password);
        System.out.println("Hashed password: " + hashedPassword);

        boolean correctPassword =
                PasswordHasher.verify("test123", hashedPassword);

        boolean wrongPassword =
                PasswordHasher.verify("wrongpassword", hashedPassword);

        System.out.println("Correct password: " + correctPassword);
        System.out.println("Wrong password: " + wrongPassword);
    }
}
