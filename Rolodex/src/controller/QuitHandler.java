package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines QuitHandler, serving "POST /api/quit". When the
 *          user clicks Quit in the browser, this handler sends back a
 *          success response (so app.js can navigate to thankyou.html)
 *          and then shuts the HttpServer down a moment later on a
 *          background thread - this is what satisfies the project
 *          requirement that the application keeps running until the
 *          user chooses to quit.
 * =====================================================================
 */
public class QuitHandler implements HttpHandler {

    // Composition: this handler "has-a" reference to the running server so it can stop it.
    private final HttpServer server;

    public QuitHandler(HttpServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendJson(exchange, 405, JsonWriter.errorJson("Method not allowed"));
            return;
        }

        HttpUtil.sendJson(exchange, 200, "{\"success\":true}");

        // Shut the server down on a separate thread, after a short delay,
        // so this response has time to actually reach the browser first.
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                // Nothing to do here - shutting down regardless.
            }
            System.out.println("User chose to quit. Shutting down the Rolodex server. Goodbye!");
            server.stop(0);
            System.exit(0);
        }).start();
    }
}
