package controller;

import java.util.List;
import model.Address;
import model.BusinessContact;
import model.Contact;
import model.FamilyContact;
import model.FriendContact;
import model.PersonalContact;
import model.SocialMediaContact;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines JsonWriter, a small, purpose-built helper for
 *          turning Contact objects into the JSON shape app.js already
 *          expects (see app.js's `contacts` array: {first, last,
 *          type, phone, email, address, f1, v1, f2, v2}). A full JSON
 *          library was intentionally avoided to keep the project to
 *          plain JDK classes only, consistent with the "no external
 *          framework" decision in the README's Decision Log - the
 *          SQLite driver remains the only external dependency.
 * =====================================================================
 */
public class JsonWriter {

    /** Converts a list of contacts (of any mixed subclass) into a JSON array string. */
    public static String toJsonArray(List<Contact> contacts) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < contacts.size(); i++) {
            if (i > 0) json.append(",");
            json.append(toJsonObject(contacts.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Converts a single contact into a JSON object matching the shape
     * app.js's card-rendering code already reads: first/last/type/
     * phone/email/address, plus generic f1/v1/f2/v2 keys for whichever
     * two fields are unique to that contact's subclass (PersonalContact
     * only uses f1/v1, since it only has one unique field, "notes").
     */
    public static String toJsonObject(Contact contact) {
        Address address = contact.getAddress();

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(contact.getId()).append(",");
        json.append("\"type\":\"").append(escape(contact.getContactType())).append("\",");
        json.append("\"first\":\"").append(escape(contact.getFirstName())).append("\",");
        json.append("\"last\":\"").append(escape(contact.getLastName())).append("\",");
        json.append("\"phone\":\"").append(escape(contact.getPhoneNumber())).append("\",");
        json.append("\"email\":\"").append(escape(contact.getEmail())).append("\",");
        json.append("\"address\":\"").append(escape(address.toString())).append("\",");
        json.append("\"street\":\"").append(escape(address.getStreet())).append("\",");
        json.append("\"city\":\"").append(escape(address.getCity())).append("\",");
        json.append("\"state\":\"").append(escape(address.getState())).append("\",");
        json.append("\"zipCode\":\"").append(escape(address.getZipCode())).append("\",");

        if (contact instanceof BusinessContact business) {
            json.append("\"f1Label\":\"Company\",\"f1Key\":\"company_name\",\"f1\":\"")
                    .append(escape(business.getCompanyName())).append("\",");
            json.append("\"f2Label\":\"Title\",\"f2Key\":\"job_title\",\"f2\":\"")
                    .append(escape(business.getJobTitle())).append("\",");
        } else if (contact instanceof FamilyContact family) {
            json.append("\"f1Label\":\"Relationship\",\"f1Key\":\"relationship\",\"f1\":\"")
                    .append(escape(family.getRelationship())).append("\",");
            json.append("\"f2Label\":\"Birthday\",\"f2Key\":\"birthday\",\"f2\":\"")
                    .append(escape(family.getBirthday())).append("\",");
        } else if (contact instanceof PersonalContact personal) {
            json.append("\"f1Label\":\"Notes\",\"f1Key\":\"notes\",\"f1\":\"")
                    .append(escape(personal.getNotes())).append("\",");
            json.append("\"f2Label\":\"\",\"f2Key\":\"\",\"f2\":\"\",");
        } else if (contact instanceof FriendContact friend) {
            json.append("\"f1Label\":\"How We Met\",\"f1Key\":\"how_we_met\",\"f1\":\"")
                    .append(escape(friend.getHowWeMet())).append("\",");
            json.append("\"f2Label\":\"Favorite Activity\",\"f2Key\":\"favorite_activity\",\"f2\":\"")
                    .append(escape(friend.getFavoriteActivity())).append("\",");
        } else if (contact instanceof SocialMediaContact socialMedia) {
            json.append("\"f1Label\":\"Platform\",\"f1Key\":\"platform\",\"f1\":\"")
                    .append(escape(socialMedia.getPlatform())).append("\",");
            json.append("\"f2Label\":\"Username\",\"f2Key\":\"username\",\"f2\":\"")
                    .append(escape(socialMedia.getUsername())).append("\",");
        }

        json.append("\"displaySummary\":\"").append(escape(contact.getLastName() + ", " + contact.getFirstName())).append("\"");
        json.append("}");
        return json.toString();
    }

    /** Builds a simple {"error": "..."} JSON payload for failed requests. */
    public static String errorJson(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    /** Escapes characters that would otherwise break JSON string syntax. */
    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
