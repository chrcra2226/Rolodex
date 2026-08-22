package repository;

import java.util.ArrayList;
import java.util.List;
import model.Contact;

/*
 * =====================================================================
 * Part 2
 * Name:    Christopher Crayton
 * Date:    August 9, 2026
 * Purpose: Defines InMemoryContactRepository, a CONCRETE class that
 *          IMPLEMENTS the ContactRepository interface (see
 *          ContactRepository.java) using a simple ArrayList held in
 *          memory. This is the "temporary" storage mechanism used for
 *          Weeks 2-3, before a real SQLite-backed implementation
 *          (SqliteContactRepository) replaces it in Week 4.
 *
 *          "implements ContactRepository" below is what makes this the
 *          class that fulfills the interface's contract - every method
 *          the interface declares must have a real body here.
 *
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Added id assignment to addContact() so this implementation
 *          behaves consistently with the new SqliteContactRepository -
 *          both now hand every contact a real, positive id, which is
 *          what Contact's equals()/hashCode() rely on.
 * =====================================================================
 */
public class InMemoryContactRepository implements ContactRepository { // <-- IMPLEMENTS the interface

    // The actual in-memory storage: a private field, only reachable
    // through the interface methods below (encapsulation).
    private List<Contact> contacts;

    // Simple counter used to hand out ids, one contact type at a time -
    // mirrors how SQLite's AUTOINCREMENT assigns ids per table.
    private int nextId = 1;

    /** Constructor - starts with an empty contact list. */
    public InMemoryContactRepository() {
        this.contacts = new ArrayList<>();
    }

    @Override
    public void addContact(Contact contact) {
        contact.setId(nextId);
        nextId++;
        contacts.add(contact);
    }

    @Override
    public boolean removeContact(Contact contact) {
        return contacts.remove(contact);
    }

    @Override
    public boolean updateContact(Contact contact) {
        // Since this implementation stores the actual object references
        // (not copies), any changes already made via the contact's own
        // setters are already reflected in the list. This method simply
        // confirms the contact is one we are actually tracking.
        return contacts.contains(contact);
    }

    @Override
    public List<Contact> getAllContacts() {
        // Return a copy so callers can't accidentally modify our
        // internal list directly - they can only go through addContact()/removeContact().
        return new ArrayList<>(contacts);
    }

    @Override
    public List<Contact> getContactsByLastNameStartingWith(char letter) {
        List<Contact> matches = new ArrayList<>();
        for (Contact contact : contacts) {
            String lastName = contact.getLastName();
            if (lastName != null && !lastName.isEmpty()
                    && Character.toLowerCase(lastName.charAt(0)) == Character.toLowerCase(letter)) {
                matches.add(contact);
            }
        }
        return matches;
    }
}

