package model;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 2, 2026
 * Purpose: Defines the Address class, a simple value object that stores
 *          a physical mailing address. This class is used to demonstrate
 *          COMPOSITION: a Contact object "has-a" Address object as one
 *          of its fields, rather than a Contact "being" an Address.
 * =====================================================================
 */

public class Address {

    // Private fields - only reachable through the getters/setters below.
    private String street;
    private String city;
    private String state;
    private String zipCode;

    /**
     * Constructor that builds a complete Address in one step.
     */
    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
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
        return street + ", " + city + ", " + state + " " + zipCode;
    }
}
