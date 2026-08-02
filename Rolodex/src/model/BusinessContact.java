package model;

/*
 * =====================================================================
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
 * =====================================================================
 */

public class BusinessContact extends Contact { // <-- INHERITANCE: "extends Contact"

    // Fields unique to a BusinessContact - these do NOT exist on the
    // base Contact class, only on this derived class.
    private String companyName;
    private String jobTitle;

    /**
     * Constructor for a BusinessContact. Notice the call to super(...)
     * below - that hands the shared fields (name, phone, email, address)
     * up to the Contact constructor to be set, so this class only has to
     * deal with the two fields that are new here.
     */
    public BusinessContact(String firstName, String lastName, String phoneNumber, String email,
                            Address address, String companyName, String jobTitle) {
        super(firstName, lastName, phoneNumber, email, address); // <-- calls the Contact (parent) constructor
        this.companyName = companyName;
        this.jobTitle = jobTitle;
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
