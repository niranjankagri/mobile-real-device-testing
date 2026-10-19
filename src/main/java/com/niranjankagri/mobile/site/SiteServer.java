package com.niranjankagri.mobile.site;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Serves PocketShop (src/main/resources/site) with the JDK's built-in HTTP server: no Node, no web server to
 * install. Real devices on BrowserStack reach it through the BrowserStack Local tunnel.
 */
public final class SiteServer implements AutoCloseable {

    private static final Map<String, String> TYPES = Map.of(
            "html", "text/html; charset=utf-8",
            "css", "text/css; charset=utf-8",
            "js", "text/javascript; charset=utf-8",
            "svg", "image/svg+xml");

    private final HttpServer server;

    /** Use start(port). */
    private SiteServer(HttpServer server) {
        this.server = server;
    }

    /** Starts the site on the given port of this machine. */
    public static SiteServer start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", SiteServer::handle);
            server.start();
            return new SiteServer(server);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not start the site on port " + port, e);
        }
    }

    /** Answers one request with a file of the site, or 404. */
    private static void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String file = path.equals("/") ? "index.html" : path.substring(1);
        // Only plain file names: no sub-folders, no "..".
        InputStream resource = file.matches("[\\w-]+\\.(html|css|js|svg)")
                ? SiteServer.class.getResourceAsStream("/site/" + file)
                : null;
        try (exchange) {
            if (resource == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] body;
            try (resource) {
                body = resource.readAllBytes();
            }
            exchange.getResponseHeaders().set("Content-Type", TYPES.get(file.substring(file.lastIndexOf('.') + 1)));
            // A device must never show an old copy from its cache after the site changed.
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
        }
    }

    /** The port the site is running on. */
    public int port() {
        return server.getAddress().getPort();
    }

    /** Stops the site at once. */
    @Override
    public void close() {
        server.stop(0);
    }
}
