package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.BusinessContact;
import model.Contact;
import model.FamilyContact;
import model.FriendContact;
import model.PersonalContact;
import model.SocialMediaContact;
import repository.ContactRepository;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines UpdateContactHandler, serving "POST
 *          /api/contacts/update". Lets the user change exactly one
 *          field on an existing contact rather than re-submitting the
 *          whole record, matching the field-by-field edit flow already
 *          built into app.js/index.html.
 *
 *          ContactRepository's updateContact(Contact) takes a whole
 *          Contact object, not a single field - so this handler first
 *          finds the existing contact (by id and type), applies just
 *          the one setter that matches the submitted field name, and
 *          then hands the whole, now-updated object to updateContact().
 * =====================================================================
 */
public class UpdateContactHandler implements HttpHandler {

    private final ContactRepository contactRepository;

    public UpdateContactHandler(ContactRepository contactRepository) {
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
            String fieldKey = fields.get("fieldKey");
            String newValue = fields.getOrDefault("newValue", "");

            Contact contact = findContact(id, contactType);
            if (contact == null) {
                HttpUtil.sendJson(exchange, 404, JsonWriter.errorJson("Contact not found."));
                return;
            }

            applyFieldUpdate(contact, fieldKey, newValue);

            boolean updated = contactRepository.updateContact(contact);
            if (updated) {
                HttpUtil.sendJson(exchange, 200, JsonWriter.toJsonObject(contact));
            } else {
                HttpUtil.sendJson(exchange, 404, JsonWriter.errorJson("Contact not found."));
            }

        } catch (IllegalArgumentException validationError) {
            HttpUtil.sendJson(exchange, 400, JsonWriter.errorJson(validationError.getMessage()));
        } catch (Exception unexpectedError) {
            HttpUtil.sendJson(exchange, 500, JsonWriter.errorJson("Server error: " + unexpectedError.getMessage()));
        }
    }

    /** Looks up a single contact by id and type from the repository's full contact list. */
    private Contact findContact(int id, String contactType) {
        List<Contact> allContacts = contactRepository.getAllContacts();
        Optional<Contact> match = allContacts.stream()
                .filter(c -> c.getId() == id && c.getContactType().equals(contactType))
                .findFirst();
        return match.orElse(null);
    }

    /**
     * Applies exactly one field update to a Contact object, based on a
     * field key matching either a shared column name (e.g.
     * "phone_number") or a type-specific one (e.g. "company_name").
     * Required fields (first_name, last_name, phone_number) are
     * validated here too, mirroring the "required" rule enforced on
     * the add-contact form.
     */
    private void applyFieldUpdate(Contact contact, String fieldKey, String newValue) {
        if ((fieldKey.equals("first_name") || fieldKey.equals("last_name") || fieldKey.equals("phone_number"))
                && newValue.trim().isEmpty()) {
            throw new IllegalArgumentException("First name, last name, and phone number cannot be blank.");
        }

        switch (fieldKey) {
            case "first_name" -> contact.setFirstName(newValue);
            case "last_name" -> contact.setLastName(newValue);
            case "phone_number" -> contact.setPhoneNumber(newValue);
            case "email" -> contact.setEmail(newValue);
            case "street" -> contact.getAddress().setStreet(newValue);
            case "city" -> contact.getAddress().setCity(newValue);
            case "state" -> contact.getAddress().setState(newValue);
            case "zip_code" -> contact.getAddress().setZipCode(newValue);
            default -> applyTypeSpecificField(contact, fieldKey, newValue);
        }
    }

    /** Handles the field keys that only exist on one specific Contact subclass. */
    private void applyTypeSpecificField(Contact contact, String fieldKey, String newValue) {
        if (contact instanceof BusinessContact business) {
            switch (fieldKey) {
                case "company_name" -> { business.setCompanyName(newValue); return; }
                case "job_title" -> { business.setJobTitle(newValue); return; }
            }
        } else if (contact instanceof FamilyContact family) {
            switch (fieldKey) {
                case "relationship" -> { family.setRelationship(newValue); return; }
                case "birthday" -> { family.setBirthday(newValue); return; }
            }
        } else if (contact instanceof PersonalContact personal) {
            if (fieldKey.equals("notes")) {
                personal.setNotes(newValue);
                return;
            }
        } else if (contact instanceof FriendContact friend) {
            switch (fieldKey) {
                case "how_we_met" -> { friend.setHowWeMet(newValue); return; }
                case "favorite_activity" -> { friend.setFavoriteActivity(newValue); return; }
            }
        } else if (contact instanceof SocialMediaContact socialMedia) {
            switch (fieldKey) {
                case "platform" -> { socialMedia.setPlatform(newValue); return; }
                case "username" -> { socialMedia.setUsername(newValue); return; }
            }
        }
        throw new IllegalArgumentException("Field '" + fieldKey + "' is not editable on a " + contact.getContactType() + " contact.");
    }
}
