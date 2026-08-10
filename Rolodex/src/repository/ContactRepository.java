package repository;

import java.util.List;
import model.Contact;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 9, 2026
 * Purpose: Defines ContactRepository, the INTERFACE that describes WHAT
 *          operations the rest of the application needs in order to
 *          store and retrieve contacts, without saying anything about
 *          HOW those operations are actually carried out.
 *
 *          This is the primary demonstration of an INTERFACE for this
 *          week. Right now, only InMemoryContactRepository implements
 *          it (see InMemoryContactRepository.java), storing contacts in
 *          a simple in-memory list. In Week 4, a second class,
 *          SqliteContactRepository, will implement this exact same
 *          interface using a real SQLite database instead. Because
 *          App.java (and, later, the web layer) only ever refers to
 *          this interface type - never to a specific implementation -
 *          the storage mechanism can be swapped from in-memory to
 *          SQLite without changing any other class in the project.
 * =====================================================================
 */
public interface ContactRepository {

    /** Adds a new contact to storage. */
    void addContact(Contact contact);

    /**
     * Removes the given contact from storage.
     * @return true if the contact was found and removed, false otherwise
     */
    boolean removeContact(Contact contact);

    /**
     * Confirms that changes made directly to a Contact object (via its
     * setters) have been saved. For an in-memory list this is close to
     * a no-op today, since the object is already the same instance in
     * memory - but declaring it here now means the SQLite version
     * (added in Week 4), which will need to run a real UPDATE
     * statement, can implement this same method name without any other
     * class needing to change.
     * @return true if the contact was found and the update was saved
     */
    boolean updateContact(Contact contact);

    /** Returns every contact currently in storage. */
    List<Contact> getAllContacts();

    /**
     * Returns every contact whose last name starts with the given
     * letter (case-insensitive).
     */
    List<Contact> getContactsByLastNameStartingWith(char letter);
}
