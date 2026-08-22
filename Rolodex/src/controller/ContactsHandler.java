package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import model.Contact;
import repository.ContactRepository;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines ContactsHandler, which serves the "/api/contacts"
 *          endpoint the View layer's app.js talks to:
 *            GET  /api/contacts  -> returns every contact as JSON
 *            POST /api/contacts  -> adds a new contact from submitted
 *                                   form fields
 *
 *          This class IMPLEMENTS the JDK's own HttpHandler interface -
 *          the same "interface = contract" idea as ContactRepository,
 *          just applied to a type supplied by the platform instead of
 *          one written for this project.
 * =====================================================================
 */
public class ContactsHandler implements HttpHandler {

    // Composition: this handler "has-a" ContactRepository it delegates to.
    // Declared as the INTERFACE type, not a specific implementation -
    // this class has no idea (and doesn't need to know) whether it's
    // talking to InMemoryContactRepository or SqliteContactRepository.
    private final ContactRepository contactRepository;

    public ContactsHandler(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGetAllContacts(exchange);
            } else if ("POST".equalsIgnoreCase(method)) {
                handleAddContact(exchange);
            } else {
                HttpUtil.sendJson(exchange, 405, JsonWriter.errorJson("Method not allowed"));
            }
        } catch (IllegalArgumentException validationError) {
            HttpUtil.sendJson(exchange, 400, JsonWriter.errorJson(validationError.getMessage()));
        } catch (Exception unexpectedError) {
            HttpUtil.sendJson(exchange, 500, JsonWriter.errorJson("Server error: " + unexpectedError.getMessage()));
        }
    }

    /** Handles GET /api/contacts - returns every contact, across all tables, as JSON. */
    private void handleGetAllContacts(HttpExchange exchange) throws IOException {
        List<Contact> contacts = contactRepository.getAllContacts();
        HttpUtil.sendJson(exchange, 200, JsonWriter.toJsonArray(contacts));
    }

    /** Handles POST /api/contacts - reads submitted form fields and creates a new contact. */
    private void handleAddContact(HttpExchange exchange) throws IOException {
        String body = HttpUtil.readRequestBody(exchange);
        Map<String, String> fields = HttpUtil.parseKeyValuePairs(body);

        if (isBlank(fields.get("firstName")) || isBlank(fields.get("lastName"))) {
            throw new IllegalArgumentException("First and last name are required.");
        }
        if (isBlank(fields.get("phoneNumber"))) {
            throw new IllegalArgumentException("Phone number is required.");
        }

        Contact newContact = ContactFactory.fromFormFields(fields);
        contactRepository.addContact(newContact);

        HttpUtil.sendJson(exchange, 201, JsonWriter.toJsonObject(newContact));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
