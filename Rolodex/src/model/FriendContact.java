package model;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 21, 2026
 * Purpose: Defines FriendContact, a fourth CHILD (derived) class of
 *          Contact - planned back in the Week 2 Software Design
 *          Document's class diagram, implemented this week. Represents
 *          a personal friend and adds two fields on top of everything
 *          Contact already provides: how the friendship started
 *          (howWeMet) and a favorite shared activity.
 *
 *          Follows the exact same pattern as BusinessContact,
 *          FamilyContact, and PersonalContact: INHERITANCE via
 *          "extends Contact", two CONSTRUCTORS (a full one and an
 *          overload without an Address that chains to the full one
 *          via this(...)), and an implementation of Contact's abstract
 *          getContactType() method.
 * =====================================================================
 */
public class FriendContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // Fields unique to a FriendContact - private, with public getters/setters.
    private String howWeMet;
    private String favoriteActivity;

    /** CONSTRUCTOR (parameterized): the full constructor for a FriendContact. */
    public FriendContact(String firstName, String lastName, String phoneNumber, String email,
                          Address address, String howWeMet, String favoriteActivity) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.howWeMet = howWeMet;
        this.favoriteActivity = favoriteActivity;
    }

    /**
     * CONSTRUCTOR (overloaded): builds a FriendContact when the Address
     * isn't known yet, chaining to the constructor above via this(...).
     */
    public FriendContact(String firstName, String lastName, String phoneNumber, String email,
                          String howWeMet, String favoriteActivity) {
        this(firstName, lastName, phoneNumber, email, new Address(), howWeMet, favoriteActivity);
    }

    public String getHowWeMet() {
        return howWeMet;
    }

    public void setHowWeMet(String howWeMet) {
        this.howWeMet = howWeMet;
    }

    public String getFavoriteActivity() {
        return favoriteActivity;
    }

    public void setFavoriteActivity(String favoriteActivity) {
        this.favoriteActivity = favoriteActivity;
    }

    /** ABSTRACTION: FriendContact's required implementation of Contact's abstract getContactType() method. */
    @Override
    public String getContactType() {
        return "Friend";
    }

    /**
     * Overrides Contact's displayInfo(), reusing the shared printing
     * logic via super.displayInfo() and adding the two fields unique
     * to a friend contact.
     */
    @Override
    public void displayInfo() {
        super.displayInfo(); // <-- reuses the parent class's printing logic
        System.out.println("How We Met:       " + howWeMet);
        System.out.println("Favorite Activity: " + favoriteActivity);
    }
}
