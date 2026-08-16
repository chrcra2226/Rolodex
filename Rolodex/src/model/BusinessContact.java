package model;

/*
 * =====================================================================
 * Part 1
 * Name:    Christopher Crayton
 * Date:    August 2, 2026
 * Purpose: Defines BusinessContact, a CHILD (derived) class of Contact.
 *          It represents a professional/work contact and adds two
 *          fields on top of everything Contact already provides: the
 *          company the person works for and their job title.
 *
 *          This class is where INHERITANCE is demonstrated in action:
 *          "extends Contact" below means BusinessContact automatically
 *          receives every field and method Contact defines (name, phone,
 *          email, address, displayInfo(), etc.) without rewriting any of
 *          it here. BusinessContact only has to add what makes it
 *          different from a generic Contact.
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 16, 2026
 * Purpose: Implemented Contact's new abstract getContactType() method
 *          (required now that Contact is abstract - see Contact.java)
 *          and added a second, overloaded CONSTRUCTOR for creating a
 *          BusinessContact before its Address is known. Reviewed access
 *          specifiers on this class; companyName/jobTitle were already
 *          private with public getters/setters, so no change was needed.
 * =====================================================================
 */

public class BusinessContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // Fields unique to a BusinessContact - these do NOT exist on the
    // base Contact class, only on this derived class. Already private,
    // confirmed appropriate during the Week 3 access-specifier review.
    private String companyName;
    private String jobTitle;

    /**
     * CONSTRUCTOR (parameterized): the full constructor for a
     * BusinessContact. Notice the call to super(...) below - that hands
     * the shared fields (name, phone, email, address) up to the Contact
     * constructor to be set, so this class only has to deal with the
     * two fields that are new here.
     */
    public BusinessContact(String firstName, String lastName, String phoneNumber, String email,
                            Address address, String companyName, String jobTitle) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.companyName = companyName;
        this.jobTitle = jobTitle;
    }

    /**
     * CONSTRUCTOR (overloaded): builds a BusinessContact when the
     * Address isn't known yet, chaining to the constructor above with
     * this(...) and reusing Address's own no-argument constructor for
     * the missing piece.
     */
    public BusinessContact(String firstName, String lastName, String phoneNumber, String email,
                            String companyName, String jobTitle) {
        this(firstName, lastName, phoneNumber, email, new Address(), companyName, jobTitle);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * ABSTRACTION: this is BusinessContact's required implementation of
     * Contact's abstract getContactType() method. Contact's own
     * displayInfo() calls this automatically, so BusinessContact never
     * has to print its own "Type:" line - it gets that behavior for
     * free just by fulfilling this one-line contract.
     */
    @Override
    public String getContactType() {
        return "Business";
    }

    /**
     * Overrides Contact's displayInfo() method. This is INHERITANCE being
     * put to use: rather than writing an entirely new print method,
     * super.displayInfo() is called first to reuse all of the shared
     * printing logic already written in Contact, and then this method
     * only adds the two extra lines that are specific to a business
     * contact.
     */
    @Override
    public void displayInfo() {
        super.displayInfo(); // <-- reuses the parent class's printing logic
        System.out.println("Company: " + companyName);
        System.out.println("Title:   " + jobTitle);
    }
}

