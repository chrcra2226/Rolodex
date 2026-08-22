package controller;

import java.util.Map;
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
 * Purpose: Defines ContactFactory, which turns the raw key/value pairs
 *          submitted from the "Add a new contact" web form (see
 *          index.html's #addContactForm and app.js) into the correct
 *          Contact subclass. Keeping "which subclass do I build, and
 *          which fields does it need" in one place avoids repeating
 *          that if/else logic inside the HTTP handler classes.
 * =====================================================================
 */
public class ContactFactory {

    /**
     * Builds a new Contact object (BusinessContact, FamilyContact, or
     * PersonalContact) from submitted form fields. The field names here
     * match the ids used in index.html's form inputs.
     */
    public static Contact fromFormFields(Map<String, String> fields) {
        String contactType = fields.getOrDefault("contactType", "");

        String firstName = fields.getOrDefault("firstName", "").trim();
        String lastName = fields.getOrDefault("lastName", "").trim();
        String phoneNumber = fields.getOrDefault("phoneNumber", "").trim();
        String email = fields.getOrDefault("email", "").trim();

        Address address = new Address(
                fields.getOrDefault("street", "").trim(),
                fields.getOrDefault("city", "").trim(),
                fields.getOrDefault("state", "").trim(),
                fields.getOrDefault("zipCode", "").trim());

        return switch (contactType) {
            case "Business" -> new BusinessContact(firstName, lastName, phoneNumber, email, address,
                    fields.getOrDefault("uniqueField1", "").trim(),   // company name
                    fields.getOrDefault("uniqueField2", "").trim());  // job title

            case "Family" -> new FamilyContact(firstName, lastName, phoneNumber, email, address,
                    fields.getOrDefault("uniqueField1", "").trim(),   // relationship
                    fields.getOrDefault("uniqueField2", "").trim());  // birthday

            case "Personal" -> new PersonalContact(firstName, lastName, phoneNumber, email, address,
                    fields.getOrDefault("uniqueField1", "").trim());  // notes

            case "Friend" -> new FriendContact(firstName, lastName, phoneNumber, email, address,
                    fields.getOrDefault("uniqueField1", "").trim(),   // how we met
                    fields.getOrDefault("uniqueField2", "").trim());  // favorite activity

            case "Social Media" -> new SocialMediaContact(firstName, lastName, phoneNumber, email, address,
                    fields.getOrDefault("uniqueField1", "").trim(),   // platform
                    fields.getOrDefault("uniqueField2", "").trim());  // username

            default -> throw new IllegalArgumentException("Unknown or missing contact type submitted: " + contactType);
        };
    }
}
