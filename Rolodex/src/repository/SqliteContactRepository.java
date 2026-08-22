package repository;

import database.DatabaseInitializer;
import model.Address;
import model.BusinessContact;
import model.Contact;
import model.FamilyContact;
import model.FriendContact;
import model.PersonalContact;
import model.SocialMediaContact;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines SqliteContactRepository, the CONCRETE class that
 *          IMPLEMENTS the ContactRepository interface (see
 *          ContactRepository.java) using a real SQLite database
 *          instead of an in-memory list. This is the class the Week 2
 *          Software Design Document planned all the way back at the
 *          start of the project: it fulfills the exact same interface
 *          as InMemoryContactRepository, which is what lets App.java
 *          (and now the web Controller layer) switch from one to the
 *          other by changing a single line - no other class in the
 *          project needed to change for this to work.
 *
 *          Each Contact subclass is stored in its own table
 *          (business_contacts, family_contacts, personal_contacts,
 *          friend_contacts, social_media_contacts), all created by
 *          DatabaseInitializer on first use. Uses plain JDBC
 *          (java.sql.*) - no ORM or external framework - to keep
 *          the project consistent with the "no external framework"
 *          decision made back in the README's Decision Log.
 *
 * Part 2
 * Name:    Christopher Crayton
 * Date:    August 21, 2026
 * Purpose: Added support for FriendContact and SocialMediaContact -
 *          the two remaining contact types planned in the Week 2
 *          design doc - completing all five contact-type tables.
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 22, 2026
 * Purpose: Fixed a bug found during testing: the no-argument
 *          constructor's default database path ("rolodex.db") was a
 *          separate hardcoded copy of the value DatabaseInitializer
 *          uses, so moving the database into its own database/ folder
 *          only actually changed one of the two places that mattered.
 *          Now references DatabaseInitializer.DEFAULT_DATABASE_FILE
 *          directly so there is only one place this path is ever
 *          written down.
 * =====================================================================
 */
public class SqliteContactRepository implements ContactRepository { // <-- IMPLEMENTS the interface

    // Private field - only this class ever needs to know the path to
    // its own database file.
    private final String databaseFilePath;

    /**
     * CONSTRUCTOR (parameterized): points this repository at a specific
     * SQLite database file. Also makes sure that file and its tables
     * exist before anything else tries to use them.
     */
    public SqliteContactRepository(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
        DatabaseInitializer.initialize(databaseFilePath);
    }

    /**
     * CONSTRUCTOR (overloaded): defaults to the same path
     * DatabaseInitializer uses ("database/rolodex.db"), chaining to the
     * constructor above with this(...) rather than repeating the
     * initialization logic. Referencing DatabaseInitializer's constant
     * here (instead of writing the path again as a separate string
     * literal) is what fixes a real bug found during testing: the two
     * classes previously had two different hardcoded defaults, so
     * changing the path in one place silently didn't change it in the
     * other.
     */
    public SqliteContactRepository() {
        this(DatabaseInitializer.DEFAULT_DATABASE_FILE);
    }

    /** Opens a fresh JDBC connection to this repository's database file. */
    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseInitializer.connectionUrlFor(databaseFilePath));
    }

    /** Maps a contact type string (from getContactType()) to its table name. */
    private static String tableNameFor(String contactType) {
        return switch (contactType) {
            case "Business" -> "business_contacts";
            case "Family" -> "family_contacts";
            case "Personal" -> "personal_contacts";
            case "Friend" -> "friend_contacts";
            case "Social Media" -> "social_media_contacts";
            default -> throw new IllegalArgumentException("Unknown contact type: " + contactType);
        };
    }

    /**
     * Returns the column names and current values for whichever fields
     * are unique to this contact's specific subclass, in a
     * LinkedHashMap so column order stays predictable when building
     * SQL. Using instanceof checks here (rather than adding a generic
     * "getUniqueColumns()" method to Contact itself) keeps the
     * database's column-naming details out of the model classes -
     * Contact and its subclasses know nothing about SQL at all.
     */
    private static LinkedHashMap<String, String> uniqueColumnsFor(Contact contact) {
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        if (contact instanceof BusinessContact business) {
            columns.put("company_name", business.getCompanyName());
            columns.put("job_title", business.getJobTitle());
        } else if (contact instanceof FamilyContact family) {
            columns.put("relationship", family.getRelationship());
            columns.put("birthday", family.getBirthday());
        } else if (contact instanceof PersonalContact personal) {
            columns.put("notes", personal.getNotes());
        } else if (contact instanceof FriendContact friend) {
            columns.put("how_we_met", friend.getHowWeMet());
            columns.put("favorite_activity", friend.getFavoriteActivity());
        } else if (contact instanceof SocialMediaContact socialMedia) {
            columns.put("platform", socialMedia.getPlatform());
            columns.put("username", socialMedia.getUsername());
        } else {
            throw new IllegalArgumentException("Unknown contact subclass: " + contact.getClass());
        }
        return columns;
    }

    // ------------------------------------------------------------------
    // addContact
    // ------------------------------------------------------------------
    @Override
    public void addContact(Contact contact) {
        String table = tableNameFor(contact.getContactType());
        LinkedHashMap<String, String> uniqueColumns = uniqueColumnsFor(contact);

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table)
                .append(" (first_name, last_name, phone_number, email, street, city, state, zip_code");
        for (String columnName : uniqueColumns.keySet()) {
            sql.append(", ").append(columnName);
        }
        sql.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?");
        sql.append(", ?".repeat(uniqueColumns.size()));
        sql.append(")");

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {

            Address address = contact.getAddress();
            int index = 1;
            statement.setString(index++, contact.getFirstName());
            statement.setString(index++, contact.getLastName());
            statement.setString(index++, contact.getPhoneNumber());
            statement.setString(index++, contact.getEmail());
            statement.setString(index++, address.getStreet());
            statement.setString(index++, address.getCity());
            statement.setString(index++, address.getState());
            statement.setString(index++, address.getZipCode());
            for (String value : uniqueColumns.values()) {
                statement.setString(index++, value);
            }

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contact.setId(generatedKeys.getInt(1)); // <-- hands the new database id back to the Contact object
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save contact: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // removeContact
    // ------------------------------------------------------------------
    @Override
    public boolean removeContact(Contact contact) {
        String table = tableNameFor(contact.getContactType());
        String sql = "DELETE FROM " + table + " WHERE id = ?";

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, contact.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete contact: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // updateContact
    // ------------------------------------------------------------------
    @Override
    public boolean updateContact(Contact contact) {
        String table = tableNameFor(contact.getContactType());
        LinkedHashMap<String, String> uniqueColumns = uniqueColumnsFor(contact);

        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ")
                .append("first_name = ?, last_name = ?, phone_number = ?, email = ?, ")
                .append("street = ?, city = ?, state = ?, zip_code = ?");
        for (String columnName : uniqueColumns.keySet()) {
            sql.append(", ").append(columnName).append(" = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            Address address = contact.getAddress();
            int index = 1;
            statement.setString(index++, contact.getFirstName());
            statement.setString(index++, contact.getLastName());
            statement.setString(index++, contact.getPhoneNumber());
            statement.setString(index++, contact.getEmail());
            statement.setString(index++, address.getStreet());
            statement.setString(index++, address.getCity());
            statement.setString(index++, address.getState());
            statement.setString(index++, address.getZipCode());
            for (String value : uniqueColumns.values()) {
                statement.setString(index++, value);
            }
            statement.setInt(index, contact.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update contact: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // getAllContacts
    // ------------------------------------------------------------------
    @Override
    public List<Contact> getAllContacts() {
        List<Contact> allContacts = new ArrayList<>();
        allContacts.addAll(queryTable("business_contacts", null));
        allContacts.addAll(queryTable("family_contacts", null));
        allContacts.addAll(queryTable("personal_contacts", null));
        allContacts.addAll(queryTable("friend_contacts", null));
        allContacts.addAll(queryTable("social_media_contacts", null));
        return allContacts;
    }

    // ------------------------------------------------------------------
    // getContactsByLastNameStartingWith
    // ------------------------------------------------------------------
    @Override
    public List<Contact> getContactsByLastNameStartingWith(char letter) {
        List<Contact> matches = new ArrayList<>();
        String prefix = String.valueOf(letter);
        matches.addAll(queryTable("business_contacts", prefix));
        matches.addAll(queryTable("family_contacts", prefix));
        matches.addAll(queryTable("personal_contacts", prefix));
        matches.addAll(queryTable("friend_contacts", prefix));
        matches.addAll(queryTable("social_media_contacts", prefix));
        return matches;
    }

    /**
     * Shared helper that queries a single table, optionally filtering
     * by a case-insensitive last-name prefix, and maps every resulting
     * row into the correct Contact subclass.
     *
     * @param tableName     which table to query
     * @param lastNamePrefix if non-null, only rows whose last name
     *                        starts with this string (case-insensitive)
     *                        are returned; if null, every row is returned
     */
    private List<Contact> queryTable(String tableName, String lastNamePrefix) {
        List<Contact> results = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName;
        if (lastNamePrefix != null) {
            sql += " WHERE LOWER(last_name) LIKE LOWER(?)";
        }
        sql += " ORDER BY last_name, first_name";

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (lastNamePrefix != null) {
                statement.setString(1, lastNamePrefix + "%");
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(mapRowToContact(tableName, resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to query " + tableName + ": " + e.getMessage(), e);
        }
        return results;
    }

    /** Converts one JDBC ResultSet row into the correct Contact subclass based on which table it came from. */
    private Contact mapRowToContact(String tableName, ResultSet row) throws SQLException {
        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phoneNumber = row.getString("phone_number");
        String email = row.getString("email");
        Address address = new Address(
                row.getString("street"), row.getString("city"),
                row.getString("state"), row.getString("zip_code"));

        Contact contact = switch (tableName) {
            case "business_contacts" -> new BusinessContact(firstName, lastName, phoneNumber, email, address,
                    row.getString("company_name"), row.getString("job_title"));
            case "family_contacts" -> new FamilyContact(firstName, lastName, phoneNumber, email, address,
                    row.getString("relationship"), row.getString("birthday"));
            case "personal_contacts" -> new PersonalContact(firstName, lastName, phoneNumber, email, address,
                    row.getString("notes"));
            case "friend_contacts" -> new FriendContact(firstName, lastName, phoneNumber, email, address,
                    row.getString("how_we_met"), row.getString("favorite_activity"));
            case "social_media_contacts" -> new SocialMediaContact(firstName, lastName, phoneNumber, email, address,
                    row.getString("platform"), row.getString("username"));
            default -> throw new IllegalStateException("Unknown table: " + tableName);
        };
        contact.setId(row.getInt("id")); // constructors don't take an id, so it's set here after construction
        return contact;
    }
}
