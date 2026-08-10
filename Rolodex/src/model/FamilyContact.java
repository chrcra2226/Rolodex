package model;

/*
 * =====================================================================
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
 * =====================================================================
 */

public class FamilyContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // Fields unique to a FamilyContact - these do NOT exist on the base
    // Contact class, only on this derived class.
    private String relationship;
    private String birthday;

    /**
     * Constructor for a FamilyContact. super(...) hands the shared
     * fields up to the Contact constructor, exactly like BusinessContact
     * does.
     */
    public FamilyContact(String firstName, String lastName, String phoneNumber, String email,
                          Address address, String relationship, String birthday) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.relationship = relationship;
        this.birthday = birthday;
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
