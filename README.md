# Rolodex Contact Manager

**Course:** SDC330L - Advanced Object-Oriented Programming Using Java LAB

**Student:** Christopher Crayton

**Project Option:** Rolodex/Contacts Application

This README is a living document for the 5-week course project. It gets
updated at the end of each week to reflect what's been built, what
decisions were made and why, and what's still ahead. If you're picking
this project back up after a break, start by reading the **Current Status** 
and **Decision Log** sections below.

## YouTube Video Link for Demonstration.

Link - https://youtu.be/XezGrruFJF0


---

## Project Overview

A contact manager that stores contacts across multiple types (Business,
Family, Personal, Friend, and Social Media - all five implemented),
supporting add, remove, update, and display (all contacts, or
filtered/searched by name).

- **Interaction model:** a real web application, served by Java's
  built-in `HttpServer` (no external framework). Weeks 1-3 used a
  console prototype to build and prove out the core classes; as of
  Week 4, the Controller layer is connected and the web UI is the
  actual, working application - open a browser to interact with it.
- **Storage model:** contacts are persisted in a real SQLite database
  (`rolodex.db`), through `SqliteContactRepository`. Weeks 2-3 used
  `InMemoryContactRepository` as a stand-in; both implement the same
  `ContactRepository` interface, which is what let the swap happen
  without changing `App.java`'s logic or any other class.

See `1.5 Project - Inheritance, Composition, and User Interactions.docx` for the full UX plan (what the user
sees on startup, how selections are made, output formatting, and how the
app is exited). and
`Week2_Software_Design_Document.docx` for the full class-by-class
architecture roadmap covering all five weeks.

---

## How to Build and Run

From the project root (the folder containing `src/`):

```bash
# Compile
javac -cp lib/sqlite-jdbc.jar -d bin src/App.java src/model/*.java src/repository/*.java src/database/*.java src/controller/*.java

# Run
java -cp "bin:lib/sqlite-jdbc.jar" App
```

> On Windows, use `;` instead of `:` in the classpath:
> `java -cp "bin;lib/sqlite-jdbc.jar" App`

Press Enter at the prompt to initialize the database (creates
`rolodex.db` on first run, with sample data) and start the web server,
then open **http://localhost:8080** in a browser. The application keeps
running - and your data keeps being saved - until you click **Quit
application** in the browser.

---

## Current Project Structure

```
ROLODEX/
├── .vscode/
│   └── settings.json             <- VS Code workspace settings (Java build/format config; safe to edit)
├── bin/                     <- compiled .class files (regenerated on build)
├── lib/
│   └── sqlite-jdbc.jar      <- SQLite JDBC driver (org.xerial, v3.42.0.0) - the only external dependency
├── src/
│   ├── App.java             <- entry point: banner/welcome, initializes the DB, seeds sample data, starts the web server
│   ├── model/                          (the "M" in MVC)
│   │   ├── Contact.java                <- ABSTRACT base class (inheritance parent) - Week 3; has-a Address (composition), has an id (Week 4)
│   │   ├── BusinessContact.java        <- derived class (extends Contact)
│   │   ├── FamilyContact.java          <- derived class (extends Contact) - Week 2
│   │   ├── PersonalContact.java        <- derived class (extends Contact) - Week 3
│   │   ├── FriendContact.java          <- derived class (extends Contact) - Week 4 fix
│   │   ├── SocialMediaContact.java     <- derived class (extends Contact) - Week 4 fix
│   │   └── Address.java                <- composed class (Contact has-a Address)
│   ├── repository/
│   │   ├── ContactRepository.java         <- interface: storage contract (Week 2)
│   │   ├── InMemoryContactRepository.java <- implements ContactRepository via ArrayList (Week 2-3)
│   │   └── SqliteContactRepository.java   <- implements ContactRepository via real SQLite (Week 4)
│   ├── database/
│   │   └── DatabaseInitializer.java    <- creates rolodex.db and its 5 tables on first run (Week 4)
│   ├── controller/                     (the "C" in MVC - Week 4)
│   │   ├── WebServer.java              <- starts HttpServer, wires up every route
│   │   ├── ContactsHandler.java        <- GET/POST /api/contacts (list, add)
│   │   ├── UpdateContactHandler.java   <- POST /api/contacts/update (edit one field)
│   │   ├── DeleteContactHandler.java   <- POST /api/contacts/delete
│   │   ├── QuitHandler.java            <- POST /api/quit (shuts down the server)
│   │   ├── ContactFactory.java         <- builds the right Contact subclass from submitted form fields
│   │   ├── JsonWriter.java             <- hand-rolled JSON serialization (no external JSON library)
│   │   └── HttpUtil.java               <- shared request/response helpers
│   └── view/                           (the "V" in MVC)
│       ├── index.html                  <- page structure: sidebar form, contacts panel, edit modal
│       ├── style.css                   <- stylesheet, contact-type color coding
│       ├── app.js                      <- fetch() calls to the Controller, card-stack rendering engine
│       └── thankyou.html               <- static confirmation page shown after quitting
└── README.md
```

> **Note:** All five contact types planned in the Week 2 Software
> Design Document's class diagram are now implemented: `BusinessContact`,
> `FamilyContact`, `PersonalContact`, `FriendContact`, and
> `SocialMediaContact`.

---

## Current Status (Week-by-Week)

| Week | Focus | Status | Notes |
|---|---|---|---|
| **Week 1** | UX design doc + inheritance/composition demo | ✅ Complete | See details below |
| **Week 2** | Software design doc + polymorphism + interface | ✅ Complete | See details below |
| **Week 3** | Constructors + access specifiers + abstract class | ✅ Complete | See details below |
| **Week 4** | SQLite database implementation + frontend/backend connection | ✅ Complete | See details below |
| **Week 5** | Bug fixes + final delivery to full spec | ⬜ Not started | |

### Week 1 details
- `Contact` (base class) and `BusinessContact` (derived class, `extends Contact`) demonstrate **inheritance**.
- `Address`, composed into `Contact` as a field, demonstrates **composition**.
- `App.java` displays the Week 1 banner/title/name, a welcome message, takes a basic keyboard input (`Scanner`, "Press Enter to continue"), then instantiates and displays 3 sample contacts.
- Deliverables submitted: `1.5 Project - Inheritance, Composition, and User Interactions.docx`, source code, and a terminal screenshot of the app running.

### Week 2 details
- Added `FamilyContact`, a second derived class (`extends Contact`), so the polymorphism demo has three distinct runtime types to show off instead of two.
- Added the `ContactRepository` **interface** (`src/repository/`), declaring `addContact`, `removeContact`, `updateContact`, `getAllContacts`, and `getContactsByLastNameStartingWith`.
- Added `InMemoryContactRepository`, which **implements** `ContactRepository` using an `ArrayList`. This is a placeholder for `SqliteContactRepository`, planned for Week 4 - both will implement the same interface, so `App.java` won't need to change when the swap happens.
- `App.java` now builds a `ContactRepository`, adds all four sample contacts through it, then loops over `getAllContacts()` calling `displayInfo()` on each - this is the **polymorphism** demonstration: the same method call produces three different outputs depending on each contact's actual runtime type.
- Deliverables submitted: `2.x Project - Software Design Document.docx` (includes a full-project class diagram covering classes planned through Week 5), updated source code, and a terminal screenshot.

### Week 3 details
- `Contact` is now **abstract**, with a new abstract method `getContactType()` - every concrete contact type is now compiler-enforced to say what kind of contact it is. `Contact`'s shared, concrete `displayInfo()` method calls this abstract method for its first line, so every subclass gets a correct "Type:" line for free.
- Added `PersonalContact`, a new derived class, since `new Contact(...)` no longer compiles once `Contact` is abstract - this fills the "Personal" category the web view's filter buttons already expected.
- **Access specifier fix found on review:** `Contact`'s fields were `protected`, but neither `BusinessContact` nor `FamilyContact` ever actually used that direct access - they only call inherited getters/setters or `super.displayInfo()`. Tightened to `private`, and `Contact`'s constructors are now `protected` (correct for an abstract class - only subclasses can ever call them).
- Added a second, overloaded **constructor** to `Address`, `Contact`, `BusinessContact`, `FamilyContact`, and `PersonalContact` - each now has a full constructor plus a shorter one (no `Address` yet) that chains to the full one via `this(...)`.
- `App.java` demonstrates the overloaded constructors directly (one `BusinessContact` is built without an address, then `setAddress()` is called afterward) and adds a dedicated loop that calls only `getContactType()`, to show the abstract method's output on its own.
- Verified the abstraction is actually enforced: attempting `new Contact(...)` from a separate test file fails to compile with `Contact is abstract; cannot be instantiated`.
- Deliverables submitted: updated source code and a terminal screenshot (no separate design document this week, per the Course Project Table - Week 3 only requires Demo Application Code & Screenshots).

### Week 4 details
- **The Controller layer exists for the first time** (`src/controller/`): `WebServer` starts Java's built-in `HttpServer`, serves `src/view/` as static files, and routes API requests to small, single-purpose handler classes (`ContactsHandler`, `UpdateContactHandler`, `DeleteContactHandler`, `QuitHandler`). This is the "C" in the project's MVC layout - it didn't exist until this week.
- **`SqliteContactRepository`** now implements `ContactRepository` using real JDBC against a SQLite database (one table per contact type: `business_contacts`, `family_contacts`, `personal_contacts`, created by `DatabaseInitializer` on first run). `App.java` only changed by one line to switch from `InMemoryContactRepository` to this - proof the interface-based design from Week 2 actually paid off.
- **`Contact` gained an `id` field** plus `equals()`/`hashCode()` overrides based on id + contact type. This was necessary because a repository now hands back freshly-built `Contact` objects on every fetch, so `removeContact(contact)`/`updateContact(contact)` needed logical identity (not Java's default reference equality) to keep working correctly.
- **`App.java` now launches the real application** instead of a throwaway console demo: it initializes the database, seeds realistic sample data on first run only, prints a quick console READ of what's stored (proving the data really came from `rolodex.db`, not memory), and then starts the web server.
- **`app.js` fully wired to the backend** - every `TODO` from Week 1-3's View-layer work is now a real `fetch()` call: loading contacts, adding a contact, editing a single field (via a new edit modal), deleting a contact, and quitting. `Friend`/`Social Media` were removed from the dropdown and filters since those model classes don't exist yet - shipping non-functional options felt worse than adding them back cleanly later.
- **Found and fixed a real rendering bug while testing with live data**: with only 1-2 search results, the flip-stack's wraparound math could point two different card positions at the same physical DOM element, and whichever rendered last (usually a blank "back" state) would silently overwrite the correct front-card display. Fixed by tracking which elements have already been rendered each frame.
- **Verified with a headless browser**, not just by eye: confirmed zero console errors, tested add/edit/delete/search/quit end-to-end through the actual UI, and confirmed data survives a full server restart by querying `rolodex.db` directly with a separate script.
- Deliverables submitted: updated source code, a terminal screenshot (database setup + sample data + console READ), and a browser screenshot of the live web application.

### Week 4 fix (post-submission)
- Added `FriendContact` and `SocialMediaContact` - the two contact types that were planned since the Week 2 design doc but never actually implemented, which meant only 3 of the 5 planned types worked. Followed the exact same pattern as the other three subclasses.
- Added the two missing SQLite tables (`friend_contacts`, `social_media_contacts`) and wired them through `SqliteContactRepository`, `ContactFactory`, `JsonWriter`, and `UpdateContactHandler`.
- Added `Friend`/`Social Media` back to the dropdown and filter buttons in `index.html`/`app.js` (the CSS color coding for both was already written from earlier design work, so no styling changes were needed).
- Diagnosed and documented a "no data, no database" report that turned out to be a run-procedure issue, not a bug - see Troubleshooting Notes.

---

## Decision Log

Running notes on choices made and why, so we don't have to re-derive the
reasoning in a later week.

- **Web-based UI over console, long-term.** A contact manager is
  fundamentally about viewing/editing many small records, which a
  form-and-list layout suits better than numbered console menus.
- **No external web framework.** Using Java's built-in
  `com.sun.net.httpserver.HttpServer` instead of Javalin/Spark, to keep
  the whole project in plain JDK classes (only SQLite's JDBC driver will
  be an external dependency, added in Week 4).
- **Five contact types**, not the minimum three: Business, Family,
  Personal, Friend, Social Media.
- **Field-by-field updates**, not whole-record re-entry, when updating a
  contact - lower risk of accidental overwrites.
- **Package structure:** split into `model` (domain classes) and
  `repository` (storage abstraction) starting Week 2. `App.java` stays in
  the default package as the entry point.
- **ContactRepository interface chosen over a display-focused interface (e.g. "Displayable").** Storage was chosen as the abstraction because
  it's the piece of the app guaranteed to change (in-memory now, SQLite
  in Week 4), which makes the "why does this need to be an interface"
  justification concrete rather than arbitrary.
- **Contact made abstract rather than left concrete.** Every real contact
  in this app is meaningfully a specific type; a generic, category-less
  Contact has no real-world meaning, so making it uninstantiable directly
  turns that rule into something the compiler enforces rather than just
  a convention. `PersonalContact` was added specifically to give the
  "no special category" case a real, concrete class of its own.
- **Contact's constructors are `protected`, not `public`.** Since Contact
  is abstract, a public constructor would be misleading - nothing outside
  a subclass can ever legally call it. `protected` says exactly what's
  true: only `super(...)` calls from subclasses use it.
- **Separate SQLite table per contact type**, not one table with a
  discriminator column. Matches the model layer's actual class hierarchy
  one-to-one, and each contact type's unique columns stay simple `TEXT`
  fields rather than a sparse table with lots of nullable columns.
- **Contact's `equals()`/`hashCode()` are based on id + contact type, not
  object identity.** Once a repository can be backed by a real database,
  "the same contact" has to mean "the same database row," not "the same
  Java object in memory" - a freshly-fetched Contact needs to be equal to
  the one the caller originally added.
- **`updateContact(Contact)` takes a whole object, not a field name.**
  Rather than widening the `ContactRepository` interface to know about
  individual field names, the Controller layer's `UpdateContactHandler`
  fetches the existing contact, applies one setter, and hands the whole
  updated object back to the same `updateContact()` method the interface
  already had. Keeps the interface's job ("persist this contact") separate
  from the web layer's job ("figure out which field the user meant").

---

## Troubleshooting Notes

Issues hit during development, and how they were resolved, in case they
resurface:

- **VS Code Java extension shows a stale "package does not match" error
  after moving/renaming files.** Fix, removed unneccessary `package`
  from `App.java` and changed paths for the imports.
- **Flip-stack card shows the wrong (blank/back) state when a search
  narrows results down to 1-2 matching contacts.** Caused by the
  wraparound math in `app.js`'s `render()` pointing more than one
  "offset" at the same physical card element when the filtered list is
  very short; whichever offset was processed last silently overwrote an
  earlier, correct render of that same element. Fixed by tracking which
  elements have already been drawn in the current `render()` call and
  skipping duplicates - found this by testing with real, filtered data
  from the database rather than only the original mockup's fixed
  7-contact dataset.
- **Opening `index.html` directly (double-click, or through a tool
  like VS Code's Live Server) shows no data and throws
  `Unexpected token 'F', "Forbidden."... is not valid JSON` when
  adding a contact.** This isn't a bug - `index.html` has no database
  connection of its own. The Java backend (which creates `rolodex.db`
  and answers requests to `/api/contacts`) only exists while `App.java`
  is running; it *is* the web server that also happens to serve this
  page. Opening the file through anything else means `/api/contacts`
  is being answered by some other tool's generic error page (HTML/text,
  not JSON), which is what breaks `response.json()`. **Correct
  procedure: run `App.java` from a terminal first, then open
  `http://localhost:8080` in a browser - never open the HTML file
  itself.**


---

## Changelog

- **2026-08-22** - Week 4 fix (round 2): sorted contacts alphabetically
  by last name on the front end (the backend returns them grouped by
  contact-type table, not alphabetically) so the letter tabs and the
  flip-through order both move A-Z correctly; color-coded the filter
  pills to match each contact type's card colors (blue Business, green
  Family, gray Personal, orange Friend, purple Social Media), including
  a light fill on whichever pill is currently active.
- **2026-08-21** - Week 4 fix: added `FriendContact` and
  `SocialMediaContact` (the two remaining contact types planned since
  Week 2), threaded through every layer - two new SQLite tables, repository
  mapping, controller JSON/form handling, and the view's dropdown/filter
  buttons. Also diagnosed and documented the "Forbidden... not valid
  JSON" issue as a run-procedure problem (must open the app via
  `http://localhost:8080` after running `App.java`, not by opening
  `index.html` directly).
- **2026-08-20** - Week 4 submitted: Controller layer added
  (`WebServer` + handler classes), `SqliteContactRepository`
  (real SQLite persistence, one table per contact type), `Contact`
  gained an `id` and `equals()`/`hashCode()`, `app.js` fully wired to
  the backend (all CRUD operations working through the live web UI),
  a real rendering bug found and fixed during testing, `App.java` now
  launches the actual application instead of a console demo.
- **2026-08-16** - Week 3 submitted: `Contact` converted to an abstract
  class with a new `getContactType()` abstract method, new
  `PersonalContact` derived class, overloaded constructors added across
  `Address`/`Contact`/`BusinessContact`/`FamilyContact`/`PersonalContact`,
  and an access-specifier fix (`Contact`'s fields tightened from
  protected to private after review showed no subclass needed direct
  access).
- **2026-08-09** - Week 2 submitted: Software Design Document (with
  full-project class diagram), `FamilyContact` (second derived class),
  `ContactRepository` interface, `InMemoryContactRepository`
  implementation, `App.java` updated to demonstrate polymorphism through
  the repository.
- **2026-08-02** - Week 1 submitted: UX design document, `Contact`/
  `BusinessContact`/`Address` classes, `App.java` demo with banner,
  welcome message, basic input, and sample contact display.
