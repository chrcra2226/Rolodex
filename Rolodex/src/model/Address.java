package model;

/*
 * =====================================================================
 * Part 1
 * Name:    Christopher Crayton
 * Date:    August 2, 2026
 * Purpose: Defines the Address class, a simple value object that stores
 *          a physical mailing address. This class is used to demonstrate
 *          COMPOSITION: a Contact object "has-a" Address object as one
 *          of its fields, rather than a Contact "being" an Address.
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 16, 2026
 * Purpose: Added a second, no-argument CONSTRUCTOR (overloading) for
 *          cases where a contact's address isn't known yet at creation
 *          time. Reviewed access specifiers on this class as part of
 *          Week 3's code review - fields were already private with
 *          public getters/setters, so no changes were needed here.
 * =====================================================================
 */

public class Address {

    // Private fields - only reachable through the getters/setters below.
    // ACCESS SPECIFIER REVIEW (Week 3): these were already private, which
    // is correct - nothing outside this class ever needs to touch street/
    // city/state/zipCode directly, only through the public accessors.
    private String street;
    private String city;
    private String state;
    private String zipCode;

    /**
     * CONSTRUCTOR (parameterized): builds a complete Address in one step.
     */
    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * CONSTRUCTOR (no-argument, overloaded): builds an empty Address for
     * situations where a contact's address isn't known yet. Rather than
     * repeating field-assignment logic, this chains to the constructor
     * above with this(...), passing empty strings for every field - one
     * of the "types of constructors" this week's assignment asks us to
     * think about (a default/no-arg constructor alongside a parameterized
     * one).
     */
    public Address() {
        this("", "", "", "");
    }

    // ---- Getters and setters ----

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Returns a single, human-readable line for display purposes,
     * e.g. "123 Main St, Norfolk, VA 23508".
     */
    @Override
    public String toString() {
        if (street.isEmpty() && city.isEmpty() && state.isEmpty() && zipCode.isEmpty()) {
            return "No address on file";
        }
        return street + ", " + city + ", " + state + " " + zipCode;
    }
}

