package model;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 16, 2026
 * Purpose: Defines PersonalContact, a new CHILD (derived) class of
 *          Contact, added in Week 3. It represents a general personal
 *          contact that doesn't fall into a more specific category like
 *          Business or Family - the "Personal" contact type already
 *          referenced by the web view's filter buttons.
 *
 *          This class exists because Contact became ABSTRACT this week
 *          (see Contact.java): Weeks 1-2 used a plain "new Contact(...)"
 *          for a contact with no special category (Maria Lopez), but
 *          that is no longer legal Java once Contact is abstract. Every
 *          contact now has to be a specific, concrete type -
 *          PersonalContact is that type for contacts that don't need
 *          any of the more specialized fields the other subclasses add.
 * =====================================================================
 */

public class PersonalContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // The one field unique to a PersonalContact: an optional free-text
    // note. Private, with a public getter/setter - consistent with the
    // access-specifier pattern used by every other Contact subclass.
    private String notes;

    /**
     * CONSTRUCTOR (parameterized): the full constructor for a
     * PersonalContact, including a note.
     */
    public PersonalContact(String firstName, String lastName, String phoneNumber, String email,
                            Address address, String notes) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.notes = notes;
    }

    /**
     * CONSTRUCTOR (overloaded): builds a PersonalContact with no note
     * yet, chaining to the constructor above via this(...) and
     * defaulting notes to an empty string, rather than duplicating the
     * field-assignment logic here.
     */
    public PersonalContact(String firstName, String lastName, String phoneNumber, String email, Address address) {
        this(firstName, lastName, phoneNumber, email, address, "");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * ABSTRACTION: PersonalContact's required implementation of
     * Contact's abstract getContactType() method.
     */
    @Override
    public String getContactType() {
        return "Personal";
    }

    /**
     * Overrides Contact's displayInfo(), reusing the shared printing
     * logic via super.displayInfo(). The Notes line only prints when
     * there actually is a note, so a PersonalContact created with the
     * short constructor doesn't show a blank "Notes:" line.
     */
    @Override
    public void displayInfo() {
        super.displayInfo(); // <-- reuses the parent class's printing logic
        if (notes != null && !notes.isEmpty()) {
            System.out.println("Notes:   " + notes);
        }
    }
}
