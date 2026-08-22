package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines HttpUtil, a small collection of static helper
 *          methods shared by every Controller class in this package:
 *          reading a request body or query string into a Map, and
 *          writing a response back to the browser. Keeping this
 *          boilerplate in one place means each handler class below
 *          only has to contain the logic that's actually specific to
 *          it (routing/CRUD), not repeated stream-reading code.
 * =====================================================================
 */
public class HttpUtil {

    /** Reads an HttpExchange's request body and decodes it as UTF-8 text. */
    public static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            inputStream.transferTo(buffer);
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }

    /**
     * Parses a "key=value&key2=value2" style string (used both for POST
     * bodies and query strings) into a Map, decoding percent-encoded
     * characters along the way.
     */
    public static Map<String, String> parseKeyValuePairs(String rawText) {
        Map<String, String> parameters = new HashMap<>();
        if (rawText == null || rawText.isBlank()) {
            return parameters;
        }
        for (String pair : rawText.split("&")) {
            String[] splitPair = pair.split("=", 2);
            String key = URLDecoder.decode(splitPair[0], StandardCharsets.UTF_8);
            String value = splitPair.length > 1
                    ? URLDecoder.decode(splitPair[1], StandardCharsets.UTF_8)
                    : "";
            parameters.put(key, value);
        }
        return parameters;
    }

    /** Extracts and parses the query string (the part after "?") from a request's URL. */
    public static Map<String, String> parseQueryParameters(HttpExchange exchange) {
        return parseKeyValuePairs(exchange.getRequestURI().getRawQuery());
    }

    /** Sends a complete HTTP response: status code, content type, and body. */
    public static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }

    public static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        sendResponse(exchange, statusCode, "application/json", json);
    }
}
