package model;

/*
 * =====================================================================
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 21, 2026
 * Purpose: Defines SocialMediaContact, a fifth CHILD (derived) class of
 *          Contact - the last of the contact types planned in the
 *          Week 2 Software Design Document's class diagram. Represents
 *          someone known mainly through a social media platform and
 *          adds two fields on top of everything Contact already
 *          provides: which platform, and the contact's username/handle
 *          on it.
 *
 *          Follows the exact same pattern as the other four Contact
 *          subclasses: INHERITANCE via "extends Contact", two
 *          CONSTRUCTORS (full + an overload without an Address that
 *          chains via this(...)), and an implementation of Contact's
 *          abstract getContactType() method.
 * =====================================================================
 */
public class SocialMediaContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // Fields unique to a SocialMediaContact - private, with public getters/setters.
    private String platform;
    private String username;

    /** CONSTRUCTOR (parameterized): the full constructor for a SocialMediaContact. */
    public SocialMediaContact(String firstName, String lastName, String phoneNumber, String email,
                               Address address, String platform, String username) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.platform = platform;
        this.username = username;
    }

    /**
     * CONSTRUCTOR (overloaded): builds a SocialMediaContact when the
     * Address isn't known yet, chaining to the constructor above via
     * this(...).
     */
    public SocialMediaContact(String firstName, String lastName, String phoneNumber, String email,
                               String platform, String username) {
        this(firstName, lastName, phoneNumber, email, new Address(), platform, username);
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /** ABSTRACTION: SocialMediaContact's required implementation of Contact's abstract getContactType() method. */
    @Override
    public String getContactType() {
        return "Social Media";
    }

    /**
     * Overrides Contact's displayInfo(), reusing the shared printing
     * logic via super.displayInfo() and adding the two fields unique
     * to a social media contact.
     */
    @Override
    public void displayInfo() {
        super.displayInfo(); // <-- reuses the parent class's printing logic
        System.out.println("Platform: " + platform);
        System.out.println("Username: " + username);
    }
}
