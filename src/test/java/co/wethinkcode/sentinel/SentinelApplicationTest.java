package co.wethinkcode.sentinel;

import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import static org.junit.jupiter.api.Assertions.*;

class SentinelApplicationTest {

     @Test
    void adminShouldAccessDashboard() {

        SentinelApplication application =
                new SentinelApplication();

    }

     @Test
    void applicationShouldCreateSuccessfully() {

        Javalin app =
                SentinelApplication.createApp();

        assertNotNull(app);

        app.stop();
    }
    @Test
    void invalidSessionShouldNotAccessAdminAlerts() {

        Javalin app =
                SentinelApplication.createApp();

        JavalinTest.test(app, (server, client) -> {

            var response =
                    client.get(
                            "/admin/alerts",
                            request -> request.header(
                                    "X-Session-Id",
                                    "invalid-session"
                            )
                    );

            assertEquals(403, response.code());
        });

        app.stop();
    }

}
