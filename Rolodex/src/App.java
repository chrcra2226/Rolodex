import controller.WebServer;
import model.Address;
import model.BusinessContact;
import model.Contact;
import model.FamilyContact;
import model.PersonalContact;
import repository.ContactRepository;
import repository.SqliteContactRepository;

import java.util.List;
import java.util.Scanner;

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
 *
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Entry point for the Week 4 Rolodex application. Previous
 *          weeks' console demo (building an in-memory repository and
 *          printing to the terminal) is replaced this week by the real
 *          application: this class now initializes a SQLite-backed
 *          ContactRepository, seeds it with realistic sample data on
 *          first run, prints a quick console summary of what's stored
 *          (demonstrating the database READ operation before the
 *          browser is even open), and then starts the Controller
 *          layer's WebServer so the user can add, update, and delete
 *          contacts through the actual web interface for the rest of
 *          the session. The application keeps running - and the
 *          database keeps the data - until the user clicks Quit in the
 *          browser (see controller/QuitHandler.java).
 * =====================================================================
 */
public class App {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        displayBanner();
        displayWelcomeMessage();
        waitForUserToContinue(keyboard); // basic INPUT operation

        // ---- CREATE the repository through the ContactRepository INTERFACE ----
        // This is the ONLY line that changed to move from Week 2-3's
        // InMemoryContactRepository to a real database - every other
        // class in the project (App included, everywhere below this
        // line) only ever refers to the ContactRepository interface
        // type, never to SqliteContactRepository directly.
        ContactRepository contactRepository = new SqliteContactRepository();

        seedSampleDataIfEmpty(contactRepository); // CREATE, if this is a first run
        displayStoredContacts(contactRepository); // READ, shown in the console

        keyboard.close();

        startWebServer(contactRepository); // hands off to the Controller layer for the rest of the session
    }

    /** Demonstrates basic INPUT: pauses and waits for the user to press Enter before continuing. */
    private static void waitForUserToContinue(Scanner keyboard) {
        System.out.print("Press Enter to initialize the database and launch the application... ");
        keyboard.nextLine();
        System.out.println();
    }

    /** Displays the required "this is Project Week 4" indicator, the assignment title, and the student's name. */
    private static void displayBanner() {
        System.out.println("=====================================================");
        System.out.println(" PROJECT WEEK 4");
        System.out.println(" Assignment: Rolodex Contact Manager - Database Interactions");
        System.out.println(" Author:     Christopher Crayton");
        System.out.println("=====================================================");
        System.out.println();
    }

    /** Displays a friendly welcome message with basic instructions for this week's application. */
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the Week 4 build of the Rolodex application!");
        System.out.println("Starting this week, contacts are stored permanently in a real");
        System.out.println("SQLite database instead of an in-memory list, and the app runs as");
        System.out.println("a full web application - add, update, delete, and search contacts");
        System.out.println("in your browser instead of the console.");
        System.out.println("(\"A database, unlike memory, actually remembers.\" - Developer)");
        System.out.println();
    }

    /**
     * CREATE: if the database is brand new (no contacts saved yet),
     * adds a few realistic sample contacts across all three contact
     * types so the application - and the database - has meaningful
     * data in it the first time it's opened, rather than starting
     * empty. On every later run, this does nothing, since the contacts
     * added here (and anything the user adds afterward) are already
     * permanently saved in rolodex.db.
     */
    private static void seedSampleDataIfEmpty(ContactRepository contactRepository) {
        if (!contactRepository.getAllContacts().isEmpty()) {
            System.out.println("Existing contacts found in the database - skipping sample data.");
            System.out.println();
            return;
        }

        System.out.println("No contacts found - adding sample data to the database...");

        Address homeAddress = new Address("214 Maple Street", "Norfolk", "VA", "23508");
        Address officeAddress = new Address("900 Commerce Way, Suite 300", "Virginia Beach", "VA", "23451");
        Address secondOfficeAddress = new Address("50 Harbor Blvd", "Norfolk", "VA", "23510");
        Address familyAddress = new Address("77 Willow Court", "Chesapeake", "VA", "23320");

        PersonalContact personalContact = new PersonalContact(
                "Maria", "Lopez", "757-555-0142", "maria.lopez@example.com",
                homeAddress, "Met at a networking event downtown.");

        BusinessContact businessContact = new BusinessContact(
                "James", "Whitfield", "757-555-0198", "j.whitfield@brightpath.com",
                officeAddress, "BrightPath Consulting", "Senior Project Manager");

        BusinessContact secondBusinessContact = new BusinessContact(
                "Priya", "Natarajan", "757-555-0177", "priya.n@coastalfinance.com",
                secondOfficeAddress, "Coastal Finance Group", "Account Executive");

        FamilyContact familyContact = new FamilyContact(
                "Devon", "Whitfield", "757-555-0111", "devon.whitfield@example.com",
                familyAddress, "Brother", "1994-03-22");

        // Each addContact() call here is a real CREATE operation - it
        // runs an actual SQL INSERT against rolodex.db and assigns the
        // new contact a database id.
        contactRepository.addContact(personalContact);
        contactRepository.addContact(businessContact);
        contactRepository.addContact(secondBusinessContact);
        contactRepository.addContact(familyContact);

        System.out.println("Sample data added: " + contactRepository.getAllContacts().size() + " contacts saved.");
        System.out.println();
    }

    /**
     * READ: retrieves every contact currently stored in the database
     * and prints how many were found, confirming the data really is
     * coming back out of database/rolodex.db (not just held in memory)
     * before the web interface is even opened. Full contact details are
     * viewable in the browser once the web server starts, so the
     * console output stays brief rather than duplicating everything.
     */
    private static void displayStoredContacts(ContactRepository contactRepository) {
        List<Contact> allContacts = contactRepository.getAllContacts();

        System.out.println("----- Contacts currently stored in database/rolodex.db -----");
        System.out.println("Total contacts: " + allContacts.size());
        System.out.println();
    }

    /** Starts the Controller layer's web server, handing off the rest of the session to the browser. */
    private static void startWebServer(ContactRepository contactRepository) {
        try {
            WebServer webServer = new WebServer(contactRepository);
            webServer.start();
        } catch (Exception startupError) {
            System.err.println("Failed to start the Rolodex web server: " + startupError.getMessage());
            startupError.printStackTrace();
            System.exit(1);
        }
    }
}
