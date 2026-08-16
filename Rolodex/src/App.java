import java.util.List;
import java.util.Scanner;
import model.Address;
import model.BusinessContact;
import model.Contact;
import model.FamilyContact;
import model.PersonalContact;
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
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 16, 2026
 * Purpose: Entry point for the Week 3 Rolodex prototype. Contact is now
 *          ABSTRACT (see Contact.java), so the old plain "new Contact(...)"
 *          call is replaced with the new PersonalContact class. This
 *          week's demo also explicitly shows the abstract getContactType()
 *          method being called polymorphically, and shows off the new
 *          overloaded CONSTRUCTORS (with/without an Address) added to
 *          every Contact subclass this week.
 * =====================================================================
 */
public class App {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        displayBanner();
        displayWelcomeMessage();
        waitForUserToContinue(keyboard); // basic INPUT operation

        ContactRepository contactRepository = buildSampleRepository();
        runDemo(contactRepository); // basic OUTPUT operations

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
     * Displays the required "this is Project Week 3" indicator, the
     * assignment title, and the student's name.
     */
    private static void displayBanner() {
        System.out.println("=====================================================");
        System.out.println(" PROJECT WEEK 3");
        System.out.println(" Assignment: Rolodex Contact Manager - Abstraction, Constructors & Access Specifiers");
        System.out.println(" Author:     Christopher Crayton");
        System.out.println("=====================================================");
        System.out.println();
    }

    /**
     * Displays a friendly welcome message with basic instructions for
     * what this prototype demonstrates.
     */
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the Week 3 prototype of the Rolodex application!");
        System.out.println("This week, Contact became an ABSTRACT class with a new");
        System.out.println("abstract method, getContactType(). Every contact subclass now");
        System.out.println("also offers multiple CONSTRUCTORS, and this week's code review");
        System.out.println("tightened a few ACCESS SPECIFIERS that were looser than needed.");
        System.out.println("(\"Some things are best left abstract - like this base class.\" - Developer)");
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
     *
     * This method also demonstrates the new overloaded CONSTRUCTORS
     * added this week: some contacts below are built with the full
     * constructor (address known up front), and others with the
     * shorter overload (address not yet known, added afterward via
     * setAddress()) - both are genuinely useful depending on what
     * information is available when a contact is first created.
     */
    private static ContactRepository buildSampleRepository() {
        ContactRepository contactRepository = new InMemoryContactRepository(); // <-- INTERFACE reference, concrete object

        Address homeAddress = new Address("214 Maple Street", "Norfolk", "VA", "23508");
        Address officeAddress = new Address("900 Commerce Way, Suite 300", "Virginia Beach", "VA", "23451");
        Address familyAddress = new Address("77 Willow Court", "Chesapeake", "VA", "23320");

        // PersonalContact (NEW derived class this week) - built with the
        // full constructor (address known up front, plus a note).
        PersonalContact personalContact = new PersonalContact(
                "Maria", "Lopez", "757-555-0142", "maria.lopez@example.com",
                homeAddress, "Met at a networking event downtown.");

        // A BusinessContact - full constructor, address known up front.
        BusinessContact businessContact = new BusinessContact(
                "James", "Whitfield", "757-555-0198", "j.whitfield@brightpath.com",
                officeAddress, "BrightPath Consulting", "Senior Project Manager");

        // A second BusinessContact - OVERLOADED CONSTRUCTOR: address is
        // not yet known, so the shorter constructor is used instead, and
        // the address is filled in afterward with setAddress() once it's
        // available. This is exactly the situation that constructor was
        // added for.
        BusinessContact secondBusinessContact = new BusinessContact(
                "Priya", "Natarajan", "757-555-0177", "priya.n@coastalfinance.com",
                "Coastal Finance Group", "Account Executive");
        secondBusinessContact.setAddress(new Address("50 Harbor Blvd", "Norfolk", "VA", "23510"));

        // A FamilyContact - full constructor, address known up front.
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
     * Retrieves every contact from the repository and displays them,
     * then separately demonstrates the abstract getContactType() method.
     */
    private static void runDemo(ContactRepository contactRepository) {
        List<Contact> allContacts = contactRepository.getAllContacts();

        System.out.println("----- Contact Directory (via ContactRepository interface) -----");
        System.out.println("Total contacts stored: " + allContacts.size());
        System.out.println();

        // ---- POLYMORPHISM IN ACTION ----
        // "contact" below is declared as type Contact, but each object
        // it refers to on a given loop iteration might actually be a
        // PersonalContact, a BusinessContact, or a FamilyContact
        // underneath. displayInfo() is concrete (defined once in
        // Contact), but its very first line calls the ABSTRACT method
        // getContactType(), so even this shared method's output differs
        // correctly for every contact type.
        for (Contact contact : allContacts) {
            contact.displayInfo(); // <-- the correct overridden version runs automatically
            System.out.println();
        }

        // ---- ABSTRACTION, called out on its own ----
        // This loop calls ONLY the abstract method, with nothing else,
        // to make plain what it provides: a guaranteed, type-correct
        // label for every contact, without a single if/else chain
        // checking "is this a BusinessContact? a FamilyContact?"
        // anywhere in this class.
        System.out.println("----- Contact Types (via the abstract getContactType() method) -----");
        for (Contact contact : allContacts) {
            System.out.println(contact.getFirstName() + " " + contact.getLastName()
                    + " -> " + contact.getContactType());
        }
        System.out.println();

        // A quick demonstration that the other interface methods work too.
        System.out.println("----- Contacts with a last name starting with 'W' -----");
        List<Contact> matchingContacts = contactRepository.getContactsByLastNameStartingWith('W');
        for (Contact contact : matchingContacts) {
            System.out.println("- " + contact.getFirstName() + " " + contact.getLastName());
        }
        System.out.println();

        System.out.println("End of Week 3 prototype. Thank you for reviewing!");
    }
}
