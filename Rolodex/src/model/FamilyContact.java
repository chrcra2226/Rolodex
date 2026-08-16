package model;

/*
 * =====================================================================
 * Part 1
 * Name:    Christopher Crayton
 * Date:    August 9, 2026
 * Purpose: Defines FamilyContact, a second CHILD (derived) class of
 *          Contact (BusinessContact, from Week 1, was the first). It
 *          represents a relative and adds two fields on top of
 *          everything Contact already provides: how the person is
 *          related (relationship) and their birthday.
 *
 *          Like BusinessContact, this class demonstrates INHERITANCE
 *          ("extends Contact"). Having a second derived class is what
 *          makes this week's polymorphism demonstration meaningful in
 *          App.java - with only one derived class, a loop over Contact
 *          references would not clearly show different behavior at
 *          runtime.
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 16, 2026
 * Purpose: Implemented Contact's new abstract getContactType() method
 *          and added a second, overloaded CONSTRUCTOR for creating a
 *          FamilyContact before its Address is known. Reviewed access
 *          specifiers; relationship/birthday were already private with
 *          public getters/setters, so no change was needed.
 * =====================================================================
 */

public class FamilyContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // Fields unique to a FamilyContact - these do NOT exist on the base
    // Contact class, only on this derived class. Already private,
    // confirmed appropriate during the Week 3 access-specifier review.
    private String relationship;
    private String birthday;

    /**
     * CONSTRUCTOR (parameterized): the full constructor for a
     * FamilyContact. super(...) hands the shared fields up to the
     * Contact constructor, exactly like BusinessContact does.
     */
    public FamilyContact(String firstName, String lastName, String phoneNumber, String email,
                          Address address, String relationship, String birthday) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.relationship = relationship;
        this.birthday = birthday;
    }

    /**
     * CONSTRUCTOR (overloaded): builds a FamilyContact when the Address
     * isn't known yet, chaining to the constructor above with this(...)
     * and reusing Address's own no-argument constructor.
     */
    public FamilyContact(String firstName, String lastName, String phoneNumber, String email,
                          String relationship, String birthday) {
        this(firstName, lastName, phoneNumber, email, new Address(), relationship, birthday);
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * ABSTRACTION: FamilyContact's required implementation of Contact's
     * abstract getContactType() method - see the matching note in
     * BusinessContact.java for why this benefits the class as a whole.
     */
    @Override
    public String getContactType() {
        return "Family";
    }

    /**
     * Overrides Contact's displayInfo(), reusing the shared printing
     * logic via super.displayInfo() and adding the two fields unique to
     * a family contact. This override is one half of this week's
     * POLYMORPHISM demonstration - see App.java for where it's actually
     * triggered through a Contact-typed reference.
     */
    @Override
    public void displayInfo() {
        super.displayInfo(); // <-- reuses the parent class's printing logic
        System.out.println("Relationship: " + relationship);
        System.out.println("Birthday:     " + birthday);
    }
}

