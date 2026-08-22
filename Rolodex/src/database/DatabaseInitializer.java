package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * =====================================================================
 * Name:    Christopher Crayton
 * Date:    August 20, 2026
 * Purpose: Defines DatabaseInitializer, a small utility class
 *          responsible for making sure the SQLite database file and
 *          all of its tables exist before SqliteContactRepository
 *          tries to use them. Separated into its own class (rather
 *          than folded into SqliteContactRepository) because "setting
 *          up the database" and "reading/writing contacts" are two
 *          different responsibilities - this class only ever runs
 *          once, at startup.
 *
 * Part 2
 * Name:    Christopher Crayton
 * Date:    August 21, 2026
 * Purpose: Added the two remaining contact-type tables,
 *          friend_contacts and social_media_contacts, completing all
 *          five tables planned in the Week 2 Software Design Document.
 *
 * Part 3
 * Name:    Christopher Crayton
 * Date:    August 22, 2026
 * Purpose: Moved the database file from the project root into its own
 *          database/ folder (database/rolodex.db instead of just
 *          rolodex.db). Added logic to create that folder if it
 *          doesn't exist yet, since SQLite creates the .db file
 *          automatically but never creates missing parent folders.
 * =====================================================================
 */
public class DatabaseInitializer {

    // Public so SqliteContactRepository (in the "repository" package)
    // can reference this exact same value instead of hardcoding its own
    // separate copy of the path - see the fix note on
    // SqliteContactRepository's no-argument constructor.
    public static final String DEFAULT_DATABASE_FILE = "database/rolodex.db";

    /**
     * Builds the JDBC connection URL for a given database file path.
     */
    public static String connectionUrlFor(String databaseFilePath) {
        return "jdbc:sqlite:" + databaseFilePath;
    }

    /**
     * Checks whether the given database file already exists; if not,
     * creates it and builds all three contact tables. Prints status
     * messages either way, so the console clearly shows what happened
     * on startup - this satisfies the Week 4 requirement that the
     * application display an informative indicator of what it's doing.
     */
    public static void initialize(String databaseFilePath) {
        File databaseFile = new File(databaseFilePath);
        boolean isFirstRun = !databaseFile.exists();

        // SQLite will create the .db FILE automatically, but it will not
        // create missing parent FOLDERS - so if databaseFilePath points
        // inside a "database" folder that doesn't exist yet, that folder
        // needs to be created here first.
        File parentFolder = databaseFile.getParentFile();
        if (parentFolder != null && !parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        if (isFirstRun) {
            System.out.println("No existing database found.");
            System.out.println("Setting up database for first use, please wait...");
        } else {
            System.out.println("Existing database found at " + databaseFile.getAbsolutePath());
        }

        try (Connection connection = DriverManager.getConnection(connectionUrlFor(databaseFilePath));
             Statement statement = connection.createStatement()) {

            statement.execute(buildCreateTableSql("business_contacts", "company_name TEXT, job_title TEXT"));
            statement.execute(buildCreateTableSql("family_contacts", "relationship TEXT, birthday TEXT"));
            statement.execute(buildCreateTableSql("personal_contacts", "notes TEXT"));
            statement.execute(buildCreateTableSql("friend_contacts", "how_we_met TEXT, favorite_activity TEXT"));
            statement.execute(buildCreateTableSql("social_media_contacts", "platform TEXT, username TEXT"));

            if (isFirstRun) {
                System.out.println("Database setup complete. Created tables: "
                        + "business_contacts, family_contacts, personal_contacts, "
                        + "friend_contacts, social_media_contacts.");
            } else {
                System.out.println("Database ready.");
            }

        } catch (SQLException e) {
            System.err.println("Fatal error setting up the database: " + e.getMessage());
            throw new RuntimeException("Unable to initialize database", e);
        }
    }

    /** Convenience overload that uses the default "rolodex.db" file name. */
    public static void initialize() {
        initialize(DEFAULT_DATABASE_FILE);
    }

    /**
     * Builds the CREATE TABLE SQL statement for one contact-type table.
     * Every contact table shares the same core columns (id, name,
     * phone, email, and the four address columns) and then adds
     * whatever type-specific columns are passed in - this is the
     * "Data structure and storage needs to make sense based on your
     * selected project" piece of the Week 4 instructions: one table
     * per Contact subclass, matching the model classes already built.
     */
    private static String buildCreateTableSql(String tableName, String uniqueColumns) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT NOT NULL, "
                + "last_name TEXT NOT NULL, "
                + "phone_number TEXT NOT NULL, "
                + "email TEXT, "
                + "street TEXT, "
                + "city TEXT, "
                + "state TEXT, "
                + "zip_code TEXT, "
                + uniqueColumns
                + ")";
    }
}
