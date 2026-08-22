package controller;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import repository.ContactRepository;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines WebServer, the class that starts the built-in Java
 *          HTTP server, wires up every URL path ("context") to the
 *          handler class responsible for it, and serves the static
 *          HTML/CSS/JS files in src/view/. This is the Controller
 *          layer of the project's MVC structure - the piece that
 *          didn't exist until this week.
 *
 *          No external web framework is used - everything here comes
 *          from com.sun.net.httpserver (built into the JDK) plus
 *          SimpleFileServer (added in Java 18) for serving static
 *          files, consistent with the "no external framework"
 *          decision recorded in the README's Decision Log.
 * =====================================================================
 */
public class WebServer {

    private static final int PORT = 8080;

    // Composition: WebServer "has-a" ContactRepository, declared as the
    // INTERFACE type. This class has no idea whether it was handed an
    // InMemoryContactRepository or a SqliteContactRepository - and
    // doesn't need to.
    private final ContactRepository contactRepository;
    private HttpServer server;

    public WebServer(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** Builds the HttpServer, registers every route, and starts listening for requests. */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Static file serving: anything under src/view/ (index.html,
        // style.css, app.js, thankyou.html) is served automatically at
        // the matching URL path.
        Path viewRootPath = Path.of("src", "view").toAbsolutePath().normalize();
        server.createContext("/", SimpleFileServer.createFileHandler(viewRootPath));

        // API endpoints - each delegates to a small, single-purpose handler class.
        server.createContext("/api/contacts", new ContactsHandler(contactRepository));
        server.createContext("/api/contacts/update", new UpdateContactHandler(contactRepository));
        server.createContext("/api/contacts/delete", new DeleteContactHandler(contactRepository));
        server.createContext("/api/quit", new QuitHandler(server));

        // Handle each incoming request on its own thread so the UI stays responsive.
        server.setExecutor(Executors.newCachedThreadPool());

        server.start();
        String url = "http://localhost:" + PORT;
        System.out.println("Rolodex is running! Open your browser to: " + url);
        System.out.println("The application will keep running until you click Quit in the browser.");

        openInBrowser(url);
    }

    /**
     * Attempts to automatically open the application in the user's
     * default web browser, so they don't have to copy/paste the URL
     * themselves. Wrapped defensively: java.awt.Desktop isn't
     * guaranteed to be available on every system (e.g., some headless
     * servers), so if it isn't, this just prints a note and the user
     * falls back to opening the URL above manually - the application
     * itself keeps running either way.
     */
    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("(Could not auto-open a browser on this system - please open the URL above manually.)");
            }
        } catch (Exception browserLaunchError) {
            System.out.println("(Could not auto-open a browser: " + browserLaunchError.getMessage()
                    + " - please open the URL above manually.)");
        }
    }
}
