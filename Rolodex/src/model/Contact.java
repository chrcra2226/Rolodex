package model;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 2, 2026
 * Purpose: Defines Contact, the BASE (parent) class for every contact
 *          stored in the Rolodex application. It holds the fields and
 *          behavior that every kind of contact shares: a name, phone
 *          number, email address, and a mailing address.
 *
 *          This class is used to demonstrate INHERITANCE: it will be
 *          extended by BusinessContact (see BusinessContact.java), which
 *          adds fields specific to a business relationship on top of
 *          everything defined here.
 *
 *          This class also demonstrates COMPOSITION: a Contact "has-a"
 *          Address object as one of its fields (see the "address" field
 *          below), rather than Contact extending Address. The Address is
 *          a separate, self-contained object that is simply owned by
 *          the Contact.
 * =====================================================================
 */

public class Contact {

    // "protected" is used here (instead of "private") specifically so
    // that BusinessContact, which will extend this class, can refer to
    // these fields directly if it needs to.
    protected String firstName;
    protected String lastName;
    protected String phoneNumber;
    protected String email;

    // ---- COMPOSITION IN ACTION ----
    // A Contact "has-a" Address. The Address class is defined completely
    // separately (see Address.java) and is simply plugged in here as a
    // field, which is what makes this composition rather than inheritance.
    protected Address address;

    /**
     * Constructor for building a new Contact with all of its shared
     * information at once.
     */
    public Contact(String firstName, String lastName, String phoneNumber, String email, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address; // the composed Address object is assigned here
    }

    // ---- Getters and setters ----

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Prints this contact's information to the console. BusinessContact
     * will override this method to add its own extra details on top of
     * what is printed here - that override is what will demonstrate
     * inheritance being put to use once we reach that class.
     */
    public void displayInfo() {
        System.out.println("Name:    " + firstName + " " + lastName);
        System.out.println("Phone:   " + phoneNumber);
        System.out.println("Email:   " + email);
        // address.toString() is called here; the Address object itself
        // is responsible for formatting its own display line.
        System.out.println("Address: " + address);
    }
}
