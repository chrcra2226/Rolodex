package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Contact;
import repository.ContactRepository;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines DeleteContactHandler, serving "POST
 *          /api/contacts/delete". Removes a single contact identified
 *          by its database id and contact type.
 * =====================================================================
 */
public class DeleteContactHandler implements HttpHandler {

    private final ContactRepository contactRepository;

    public DeleteContactHandler(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendJson(exchange, 405, JsonWriter.errorJson("Method not allowed"));
            return;
        }

        try {
            Map<String, String> fields = HttpUtil.parseKeyValuePairs(HttpUtil.readRequestBody(exchange));
            int id = Integer.parseInt(fields.get("id"));
            String contactType = fields.get("contactType");

            List<Contact> allContacts = contactRepository.getAllContacts();
            Optional<Contact> match = allContacts.stream()
                    .filter(c -> c.getId() == id && c.getContactType().equals(contactType))
                    .findFirst();

            if (match.isEmpty()) {
                HttpUtil.sendJson(exchange, 404, JsonWriter.errorJson("Contact not found."));
                return;
            }

            boolean removed = contactRepository.removeContact(match.get());
            if (removed) {
                HttpUtil.sendJson(exchange, 200, "{\"success\":true}");
            } else {
                HttpUtil.sendJson(exchange, 404, JsonWriter.errorJson("Contact not found."));
            }

        } catch (Exception unexpectedError) {
            HttpUtil.sendJson(exchange, 500, JsonWriter.errorJson("Server error: " + unexpectedError.getMessage()));
        }
    }
}
