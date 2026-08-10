import java.util.List;
import java.util.Scanner;
import model.Address;
import model.BusinessContact;
import model.Contact;
import model.FamilyContact;
import repository.ContactRepository;
import repository.InMemoryContactRepository;
/*
 * =====================================================================
 * Part 1
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
 * 
 * Part 2
 * Name:    Christopher Crayton
 * Date:    August 9, 2026
 * Purpose: Entry point for the Week 2 Rolodex prototype. Building on
 *          Week 1's Contact/BusinessContact/Address classes, this week
 *          adds a second derived class (FamilyContact), an INTERFACE
 *          (ContactRepository) with a concrete implementing class
 *          (InMemoryContactRepository), and a clear demonstration of
 *          POLYMORPHISM: a single Contact-typed reference is used to
 *          call displayInfo() on objects of three different runtime
 *          types, and the correct overridden version runs automatically
 *          each time.
 * =====================================================================
 */
public class App {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        displayBanner();
        displayWelcomeMessage();
        waitForUserToContinue(keyboard); // basic INPUT operation

        ContactRepository contactRepository = buildSampleRepository();
        runPolymorphismDemo(contactRepository); // basic OUTPUT operations

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
     * Displays the required "this is Project Week 2" indicator, the
     * assignment title, and the student's name.
     */
    private static void displayBanner() {
        System.out.println("=====================================================");
        System.out.println(" PROJECT WEEK 2");
        System.out.println(" Assignment: Rolodex Contact Manager - Interface & Polymorphism Prototype");
        System.out.println(" Author:     Christopher Crayton");
        System.out.println("=====================================================");
        System.out.println();
    }

    /**
     * Displays a friendly welcome message with basic instructions for
     * what this prototype demonstrates.
     */
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the Week 2 prototype of the Rolodex application!");
        System.out.println("This week builds on Week 1 by adding a second derived contact");
        System.out.println("type (FamilyContact), a ContactRepository INTERFACE for storing");
        System.out.println("contacts, and a demonstration of POLYMORPHISM: contacts of");
        System.out.println("different types are displayed through a single, shared method call.");
        System.out.println("(\"You are never too young for a rolodex!\" - Developer)");
        System.out.println();
    }

    /**
     * Builds a repository (through the ContactRepository INTERFACE type)
     * and populates it with realistic sample contacts of three different
     * types, using its addContact() method.
     *
     * Notice the declared type on the left is the INTERFACE,
     * ContactRepository, not InMemoryContactRepository. Everywhere else
     * in this class only ever refers to that interface type - which is
     * what will let Week 4 swap in a SQLite-backed repository without
     * this method (or any other) needing to change.
     */
    private static ContactRepository buildSampleRepository() {
        ContactRepository contactRepository = new InMemoryContactRepository(); // <-- INTERFACE reference, concrete object

        Address homeAddress = new Address("214 Maple Street", "Norfolk", "VA", "23508");
        Address officeAddress = new Address("900 Commerce Way, Suite 300", "Virginia Beach", "VA", "23451");
        Address secondOfficeAddress = new Address("50 Harbor Blvd", "Norfolk", "VA", "23510");
        Address familyAddress = new Address("77 Willow Court", "Chesapeake", "VA", "23320");

        // A plain Contact (the BASE class).
        Contact personalContact = new Contact(
                "Maria", "Lopez", "757-555-0142", "maria.lopez@example.com", homeAddress);

        // A BusinessContact (DERIVED class #1, from Week 1).
        BusinessContact businessContact = new BusinessContact(
                "James", "Whitfield", "757-555-0198", "j.whitfield@brightpath.com",
                officeAddress, "BrightPath Consulting", "Senior Project Manager");

        BusinessContact secondBusinessContact = new BusinessContact(
                "Priya", "Natarajan", "757-555-0177", "priya.n@coastalfinance.com",
                secondOfficeAddress, "Coastal Finance Group", "Account Executive");

        // A FamilyContact (DERIVED class #2, new this week).
        FamilyContact familyContact = new FamilyContact(
                "Devon", "Whitfield", "757-555-0111", "devon.whitfield@example.com",
                familyAddress, "Brother", "1994-03-22");

        // Every add here goes through the INTERFACE method addContact() -
        // the repository doesn't need to know or care which concrete
        // Contact subclass it was handed.
        contactRepository.addContact(personalContact);
        contactRepository.addContact(businessContact);
        contactRepository.addContact(secondBusinessContact);
        contactRepository.addContact(familyContact);

        return contactRepository;
    }

    /**
     * Retrieves every contact from the repository and displays them.
     * This is where POLYMORPHISM is demonstrated: getAllContacts()
     * returns a List&lt;Contact&gt;, but the objects inside it are
     * actually a mix of Contact, BusinessContact, and FamilyContact.
     * Every loop iteration below calls the exact same method,
     * contact.displayInfo(), yet three different versions of that
     * method run depending on each object's real, runtime type - Java
     * decides which override to run automatically. No if/else chain
     * checking each contact's type was needed to make that happen.
     */
    private static void runPolymorphismDemo(ContactRepository contactRepository) {
        List<Contact> allContacts = contactRepository.getAllContacts();

        System.out.println("----- Contact Directory (via ContactRepository interface) -----");
        System.out.println("Total contacts stored: " + allContacts.size());
        System.out.println();

        // ---- POLYMORPHISM IN ACTION ----
        // "contact" below is declared as type Contact, but each object
        // it refers to on a given loop iteration might actually be a
        // Contact, a BusinessContact, or a FamilyContact underneath.
        for (Contact contact : allContacts) {
            contact.displayInfo(); // <-- the correct overridden version runs automatically
            System.out.println();
        }

        // A quick demonstration that the other interface methods work too.
        System.out.println("----- Contacts with a last name starting with 'W' -----");
        List<Contact> matchingContacts = contactRepository.getContactsByLastNameStartingWith('W');
        for (Contact contact : matchingContacts) {
            System.out.println("- " + contact.getFirstName() + " " + contact.getLastName());
        }
        System.out.println();

        System.out.println("End of Week 2 prototype. Thank you for reviewing!");
    }
}
