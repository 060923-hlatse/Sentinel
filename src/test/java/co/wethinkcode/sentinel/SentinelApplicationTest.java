package co.wethinkcode.sentinel;

import org.junit.jupiter.api.Test;

import io.javalin.Javalin;

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


}
