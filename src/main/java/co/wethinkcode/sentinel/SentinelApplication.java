package co.wethinkcode.sentinel;

import co.wethinkcode.sentinel.repository.SecurityEventRepository;
import co.wethinkcode.sentinel.repository.UserRepository;
import co.wethinkcode.sentinel.security.AuthenticationService;
import co.wethinkcode.sentinel.security.AuthorizationService;
import io.javalin.Javalin;

public class SentinelApplication {

    public static void main(String[] args) {

        // Repositories
        UserRepository userRepository =
                new UserRepository();

        SecurityEventRepository securityEventRepository =
                new SecurityEventRepository();

        // Services
        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository,
                        securityEventRepository
                );

        AuthorizationService authorizationService =
                new AuthorizationService(
                        securityEventRepository
                );

        // Start server
        Javalin app = Javalin.create()
                .start(7000);

        // Health endpoint
        app.get("/health", ctx -> {
            ctx.json("Sentinel is running");
        });

        // Login endpoint
        app.post("/login", ctx -> {

            String username =
                    ctx.formParam("username");

            String password =
                    ctx.formParam("password");

            String ipAddress =
                    ctx.ip();

            boolean authenticated =
                    authenticationService.login(
                            username,
                            password,
                            ipAddress
                    );

            if (authenticated) {
                ctx.status(200);
                ctx.json("Login successful");
            } else {
                ctx.status(401);
                ctx.json("Login failed");
            }
        });

        
        app.get("/admin/dashboard", ctx -> {

            String username =
                    ctx.queryParam("username");

            String ipAddress =
                    ctx.ip();

            var user =
                    userRepository.findByUsername(username);

            boolean allowed =
                    authorizationService.canAccessAdminArea(
                            user,
                            ipAddress
                    );

            if (!allowed) {
                ctx.status(403);
                ctx.json("Access denied");
                return;
            }

            ctx.status(200);
            ctx.json("Welcome to the Sentinel admin dashboard");
        });
    }
}