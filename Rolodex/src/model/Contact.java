package model;

/*
 * =====================================================================
 * Part 1
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
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 16, 2026
 * Purpose: Converted Contact into an ABSTRACT class and added an
 *          abstract method, getContactType(). Beyond the ContactRepository
 *          interface from Week 2, this is the other place abstraction
 *          makes sense in this project: every contact is meaningfully
 *          one of a specific type (Business, Family, Personal, and
 *          eventually Friend/Social Media), and there is no real-world
 *          meaning to a plain, generic "Contact" with no category. Making
 *          Contact abstract makes that a compile-time rule instead of
 *          just a convention - "new Contact(...)" will no longer compile
 *          anywhere in the project.
 *
 *          Also tightened the shared fields from protected to private
 *          (see the ACCESS SPECIFIER REVIEW note below) and added a
 *          second, overloaded CONSTRUCTOR.
 *
 * Part 4
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Added an "id" field, with a getter/setter and overridden
 *          equals()/hashCode(), so contacts can be reliably identified
 *          once they're persisted in a database (see the new
 *          SqliteContactRepository). A ContactRepository implementation
 *          hands back freshly-built Contact objects on every fetch, so
 *          Java's default reference equality is no longer enough for
 *          removeContact(contact)/updateContact(contact) to find the
 *          right row - equals() now compares id and contact type
 *          instead.
 * =====================================================================
 */

public abstract class Contact { // <-- ABSTRACTION: "abstract" means this class cannot be instantiated directly

    // ACCESS SPECIFIER REVIEW (Week 3): these fields were "protected" in
    // Weeks 1-2, on the assumption that derived classes like
    // BusinessContact and FamilyContact would need direct access to
    // them. Reviewing the actual subclasses shows that assumption was
    // wrong - every subclass only ever calls the inherited public
    // getters/setters (or super.displayInfo()), never the fields
    // themselves. Since nothing outside this class needs direct access,
    // "private" is the more correct, least-privilege choice, so these
    // were tightened from protected to private this week.
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    // Added Week 4: a database-assigned identifier. 0 means "not yet
    // saved" - a repository implementation is responsible for setting
    // this to a real value once the contact has been persisted.
    private int id = 0;

    // ---- COMPOSITION IN ACTION ----
    // A Contact "has-a" Address. The Address class is defined completely
    // separately (see Address.java) and is simply plugged in here as a
    // field, which is what makes this composition rather than inheritance.
    private Address address;

    /**
     * CONSTRUCTOR (parameterized): builds a new Contact with all of its
     * shared information at once, including a specific Address.
     *
     * This constructor is declared "protected" rather than "public" -
     * another ACCESS SPECIFIER decision worth calling out. Because
     * Contact is abstract, "new Contact(...)" can never be written
     * anywhere, even inside this same package, so a public constructor
     * would be misleading. "protected" correctly communicates that only
     * subclasses (via super(...)) are meant to call it.
     */
    protected Contact(String firstName, String lastName, String phoneNumber, String email, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address; // the composed Address object is assigned here
    }

    /**
     * CONSTRUCTOR (overloaded): builds a new Contact without yet
     * knowing its Address, chaining to the constructor above via
     * this(...) and supplying an empty Address (see Address's own
     * no-argument constructor). This is the kind of constructor
     * overloading the Week 3 assignment asks us to consider - not
     * every caller will have a complete address on hand right away.
     */
    protected Contact(String firstName, String lastName, String phoneNumber, String email) {
        this(firstName, lastName, phoneNumber, email, new Address());
    }

    // ---- Getters and setters (all public - this data legitimately
    // needs to be readable/writable from outside the class, e.g. by
    // ContactRepository implementations and the eventual web layer) ----

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
     * The database-assigned id for this contact. 0 means this contact
     * has not been saved yet.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets this contact's id. This is called by a ContactRepository
     * implementation right after a new contact is successfully saved -
     * application code should not normally need to call this directly.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Two contacts are considered equal if they have the same database
     * id AND the same contact type. The type is included because each
     * contact type is stored in its own table with its own separate
     * AUTOINCREMENT id sequence (see SqliteContactRepository), so a
     * Business contact with id 3 and a Family contact with id 3 are two
     * completely different rows, not the same contact. This override is
     * what lets removeContact(contact)/updateContact(contact) keep
     * working correctly even when "contact" is a freshly-fetched object
     * from the database rather than the exact same object instance the
     * caller created earlier.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Contact)) return false;
        Contact that = (Contact) other;
        return this.id != 0 && this.id == that.id && this.getContactType().equals(that.getContactType());
    }

    @Override
    public int hashCode() {
        return getContactType().hashCode() * 31 + id;
    }

    /**
     * ABSTRACTION: every concrete subclass MUST provide its own
     * implementation of this method - the compiler enforces it, so it
     * is impossible to add a new contact type and forget to say what
     * kind of contact it is. BusinessContact returns "Business",
     * FamilyContact returns "Family", PersonalContact returns
     * "Personal", and so on.
     */
    public abstract String getContactType();

    /**
     * Prints this contact's information to the console. This method is
     * concrete (not abstract) because the printing logic itself is
     * identical for every contact type - only the extra, type-specific
     * lines differ, which is why subclasses override this method and
     * call super.displayInfo() to reuse everything printed here.
     *
     * Notice the first line calls getContactType() - the ABSTRACT
     * method above. Every subclass automatically gets a correct,
     * consistent "Type:" line for free, without writing that line
     * itself, simply by implementing getContactType(). That is the
     * concrete benefit of the abstraction: shared behavior (this
     * method) can depend on subclass-specific behavior (getContactType())
     * without Contact needing to know which subclass it's actually
     * running on.
     */
    public void displayInfo() {
        System.out.println("Type:    " + getContactType()); // <-- calls the abstract method polymorphically
        System.out.println("Name:    " + firstName + " " + lastName);
        System.out.println("Phone:   " + phoneNumber);
        System.out.println("Email:   " + email);
        // address.toString() is called here; the Address object itself
        // is responsible for formatting its own display line.
        System.out.println("Address: " + address);
    }
}

