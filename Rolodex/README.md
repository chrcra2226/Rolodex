# Rolodex Contact Manager

**Course:** SDC330L - Advanced Object-Oriented Programming Using Java LAB
**Student:** Christopher Crayton
**Project Option:** Rolodex/Contacts Application

This README is a living document for the 5-week course project. It gets
updated at the end of each week to reflect what's been built, what
decisions were made and why, and what's still ahead. If you're picking
this project back up after a break, start by reading the **Current Status** 
and **Decision Log** sections below.

---

## Project Overview

A contact manager that stores contacts across multiple types (Business,
Family, Friend, Social Media), supporting add, remove, update, and
display (all contacts, or filtered by last-name-starting-letter).

- **Final interaction model:** web-based UI, served by Java's built-in
  `HttpServer` (no external framework), backed by a SQLite database.
- **Current interaction model (Week 1):** console-based prototype, used
  to build and prove out the core classes before the web layer is added.

See `1.5 Project - Inheritance, Composition, and User Interactions.docx` for the full UX plan (what the user
sees on startup, how selections are made, output formatting, and how the
app is exited).

---

## How to Build and Run

From the project root (the folder containing `src/`):

```bash
# Compile
javac -d bin src/model/*.java

# Run
java -cp bin model.App
```

> Once the SQLite driver is added (Week 3-4), the classpath will need to
> include the driver jar in `lib/`, e.g.
> `java -cp "bin:lib/sqlite-jdbc.jar" model.App` (use `;` instead of `:`
> on Windows).

---

## Current Project Structure

```
ROLODEX/
├── .vscode/
│   └── settings.json             <- VS Code workspace settings (Java build/format config; safe to edit)
├── bin/                     <- compiled .class files (regenerated on build)
├── lib/                     <- external jars will go here (e.g. sqlite-jdbc.jar, added in a later week)
├── src/
│   ├── App.java             <- entry point: banner, welcome message, class demo
│   └── model/
│       ├── Contact.java         <- base class (inheritance parent)
│       ├── BusinessContact.java <- derived class (extends Contact)
│       └── Address.java         <- composed class (Contact has-a Address)
└── README.md
```

> **Note:** everything currently lives in a single `model` package. That's
> fine for now, but as the project grows in later weeks (a repository
> layer, a web server layer, etc.), those new classes will need their own
> distinctly-named packages (e.g. `repository`, `webserver`) so they don't
> collide with `model`.

---

## Current Status (Week-by-Week)

| Week | Focus | Status | Notes |
|---|---|---|---|
| **Week 1** | UX design doc + inheritance/composition demo | ✅ Complete | See details below |
| **Week 2** | Software design doc + polymorphism + interface | ⬜ Not started | |
| **Week 3** | Constructors + access specifiers + abstract class | ⬜ Not started | |
| **Week 4** | SQLite database implementation | ⬜ Not started | |
| **Week 5** | Bug fixes + final delivery to full spec | ⬜ Not started | |

### Week 1 details
- `Contact` (base class) and `BusinessContact` (derived class, `extends Contact`) demonstrate **inheritance**.
- `Address`, composed into `Contact` as a field, demonstrates **composition**.
- `App.java` displays the Week 1 banner/title/name, a welcome message, takes a basic keyboard input (`Scanner`, "Press Enter to continue"), then instantiates and displays 3 sample contacts.
- Deliverables submitted: `1.5 Project - Inheritance, Composition, and User Interactions.docx`, source code, and a terminal screenshot of the app running.

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
- **Four contact types**, not the minimum three: Business, Family,
  Friend, Social Media.
- **Field-by-field updates**, not whole-record re-entry, when updating a
  contact - lower risk of accidental overwrites.
- **Package structure:** currently flattened to a single `model` package
  for Week 1's small class set. Will need to split into multiple packages
  (`model`, `repository`, `service`, `webserver`, etc.) as more layers are
  added - see Project Structure note above.

---

## Troubleshooting Notes

Issues hit during development, and how they were resolved, in case they
resurface:

- **VS Code Java extension shows a stale "package does not match" error
  after moving/renaming files.** Fix, removed unneccessary `package`
  from `App.java` and changed paths for the imports.


---

## Changelog

- **2026-08-02** - Week 1 submitted: UX design document, `Contact`/
  `BusinessContact`/`Address` classes, `App.java` demo with banner,
  welcome message, basic input, and sample contact display.
