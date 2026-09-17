package co.wethinkcode.sentinel;

import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.FormBody;
import okhttp3.Request;

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

    @Test
    void adminShouldAccessAlerts() {

        Javalin app =
                SentinelApplication.createApp();

        JavalinTest.test(app, (server, client) -> {

      var loginResponse =
        client.request(
                "/login",
                new Request.Builder()
                        .post(
                                new FormBody.Builder()
                                        .add("username", "admin_sarah")
                                        .add("password", "admin123")
                                        .build()
                        )
        );

        assertEquals(200, loginResponse.code());

        String sessionId = loginResponse.body().string();
                var response = client.get(
                            "/admin/alerts",
                            request -> request.header(
                                    "X-Session-Id",
                                    sessionId
                            )
                    );

            assertEquals(200, response.code());
        });

        app.stop();
    }

}
