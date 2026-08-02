import java.util.Scanner;
import model.Address;
import model.BusinessContact;
import model.Contact;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 2, 2026
 * Purpose: Entry point for the Week 1 Rolodex prototype. This class
 *          displays the required Week 1 banner and welcome message,
 *          reads a basic keyboard input from the user (demonstrating
 *          basic input/output), then creates a few realistic Contact
 *          and BusinessContact objects and displays their information,
 *          demonstrating both INHERITANCE (BusinessContact extends
 *          Contact) and COMPOSITION (every Contact has-a Address) in
 *          action.
 * =====================================================================
 */
public class App {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        displayBanner();
        displayWelcomeMessage();
        waitForUserToContinue(keyboard); // basic INPUT operation
        runContactDemo();                // basic OUTPUT operations

        keyboard.close();
    }

    /**
     * Demonstrates basic INPUT: pauses and waits for the user to press
     * Enter before continuing. This is a small, deliberately simple
     * example of reading from the keyboard - later weeks will replace
     * this with a real menu (Add / Update / Delete / Display / Quit).
     */
    private static void waitForUserToContinue(Scanner keyboard) {
        System.out.print("Press Enter to view the sample contacts... ");
        keyboard.nextLine(); // reads and discards the Enter key press
        System.out.println();
    }

    /**
     * Displays the required "this is Project Week 1" indicator, the
     * assignment title, and the student's name.
     */
    private static void displayBanner() {
        System.out.println("=====================================================");
        System.out.println(" PROJECT WEEK 1");
        System.out.println(" Assignment: Rolodex Contact Manager - Class Prototype");
        System.out.println(" Author:     Christopher Crayton");
        System.out.println("=====================================================");
        System.out.println();
    }

    /**
     * Displays a friendly welcome message with basic instructions for
     * what this prototype demonstrates.
     */
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the Week 1 prototype of the Rolodex application!");
        System.out.println("This early version doesn't manage a real contact list yet - it");
        System.out.println("simply builds a few sample contacts in memory and displays them");
        System.out.println("below so you can see the Contact and BusinessContact classes at");
        System.out.println("work. A full Add/Update/Delete/Search menu is coming in a later week.");
        System.out.println("(\"You are never too young for a rolodex!\" - Developer)");
        System.out.println();
    }

    /**
     * Builds a few contacts with realistic sample information and prints
     * their details to the console.
     */
    private static void runContactDemo() {

        // ---- COMPOSITION: build Address objects first, then hand them
        // to Contact/BusinessContact constructors, which store them as a field. ----
        Address homeAddress = new Address("214 Maple Street", "Norfolk", "VA", "23508");
        Address officeAddress = new Address("900 Commerce Way, Suite 300", "Virginia Beach", "VA", "23451");

        // A plain Contact (the BASE class) - a personal, non-business contact.
        Contact personalContact = new Contact(
                "Maria", "Lopez", "757-555-0142", "maria.lopez@example.com", homeAddress);

        // A BusinessContact (the DERIVED class) - inherits everything
        // Contact provides, plus its own companyName/jobTitle fields.
        BusinessContact businessContact = new BusinessContact(
                "James", "Whitfield", "757-555-0198", "j.whitfield@brightpath.com",
                officeAddress, "BrightPath Consulting", "Senior Project Manager");

        // A second BusinessContact, to show the class being reused with different data.
        BusinessContact secondBusinessContact = new BusinessContact(
                "Priya", "Natarajan", "757-555-0177", "priya.n@coastalfinance.com",
                new Address("50 Harbor Blvd", "Norfolk", "VA", "23510"),
                "Coastal Finance Group", "Account Executive");

        System.out.println("----- Personal Contact (base Contact class) -----");
        personalContact.displayInfo(); // calls Contact's own displayInfo()
        System.out.println();

        System.out.println("----- Business Contact #1 (BusinessContact - derived class) -----");
        businessContact.displayInfo(); // calls BusinessContact's overridden displayInfo()
        System.out.println();

        System.out.println("----- Business Contact #2 (BusinessContact - derived class) -----");
        secondBusinessContact.displayInfo();
        System.out.println();

        System.out.println("End of Week 1 prototype. Thank you for reviewing!");
    }
}
