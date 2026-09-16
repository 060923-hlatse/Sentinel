package co.wethinkcode.sentinel;

import co.wethinkcode.sentinel.repository.SecurityEventRepository;
import co.wethinkcode.sentinel.repository.UserRepository;
import co.wethinkcode.sentinel.security.AuthenticationService;
import co.wethinkcode.sentinel.security.AuthorizationService;
import io.javalin.Javalin;
import co.wethinkcode.sentinel.security.SessionManager;

public class SentinelApplication {

    public static void main(String[] args) {
        createApp().start(7000);
    }

    public static Javalin createApp() {

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

        SessionManager sessionManager =
        new SessionManager();        

        // Start server
        Javalin app = Javalin.create();

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

                var user = userRepository.findByUsername(username);

                String sessionId =
                        sessionManager.createSession(user);

                ctx.status(200);
                ctx.json(sessionId);

                } else {

                ctx.status(401);
                ctx.json("Login failed");
                }
        });

       app.get("/admin/dashboard", ctx -> {

        String sessionId =
                ctx.header("X-Session-Id");

        String ipAddress =
                ctx.ip();

        var user =
                sessionManager.getUser(sessionId);

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

                var events =
                        securityEventRepository.findRecentEvents();

                ctx.json(events);
                });

        app.post("/logout", ctx -> {

                String sessionId =
                        ctx.header("X-Session-Id");

                sessionManager.removeSession(sessionId);

                ctx.status(200);
                ctx.json("Logged out successfully");
                });
                return app;
        }
}