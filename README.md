# Taste Heaven — Urban Style Restaurant Management System

**A cross-platform (Android + Desktop) restaurant management solution** that connects Customers, Waiters, Chefs, and Managers through a single, role-based, real-time system — replacing manual order-taking, paper reservations, and disconnected stock tracking with one streamlined digital workflow.

Built as a capstone project for the **Mobile App Development/Programming II** module at **Sol Plaatje University** by **Menzi Sigwebela** (Urban Style Innovators).

> Customers browse the menu, order food, and reserve tables from their phone. Waiters see orders come in live and manage delivery. Chefs get a real-time prep queue. Managers control the menu, stock, and reservations, and watch revenue and usage stats update as it happens.

---

## Tech Stack

| Layer | Android | Desktop |
|---|---|---|
| **Language** | Java (Android SDK) | Java (Swing) |
| **UI Framework** | Android Views / RecyclerView | Java Swing (JFrame/JPanel) |
| **Backend / Data** | Firebase (Realtime Database) | MySQL |
| **Authentication** | Firebase Authentication | BCrypt-hashed credentials (MySQL) |
| **Data Transport** | HTTPS/TLS, async Firebase queries | JDBC, MySQL transactions |
| **Encryption** | Firebase-managed password encryption | BCrypt (passwords) + AES (sensitive data at rest) |
| **Build Tooling** | Gradle -> APK | Packaged as a standalone `.JAR` (incl. MySQL Connector/J) |
| **Compatibility** | Android 10 (API 29) and above | Windows, macOS, Linux — Java 8+ |

**Architecture:** Role-based access control (Customer / Waiter / Chef / Manager) on top of a relational schema normalized up to **4NF**, designed to eliminate redundancy and keep order, reservation, and inventory data consistent across both platforms in real time.

---

## Key Features

- Secure role-based login and registration — Customer, Waiter, Chef, Manager
- Categorized menu browsing with images, prices, and live availability
- Cart and checkout flow with automatic total calculation
- Live table reservations — pick a date, time, guest count, and table (with view type: garden, window, main hall)
- Real-time order tracking — Submitted, Accepted, Being Prepared, Ready, Delivered
- Chef queue for accepting and marking orders as ready
- Waiter dashboard for accepting, processing, and delivering orders
- Manager control panel — manage menu items, monitor stock/sales, approve or decline reservations, and view live stats (users, income, reservations)
- Post-order feedback system with star ratings and optional anonymous submission
- POPIA-compliant data handling, encrypted transmission, and hashed credential storage

---

## System Design

The system was designed around four core stakeholders (Customer, Waiter, Chef, Manager) interacting with a shared backend through a Context Diagram and Use Case Diagram, and a relational schema normalized from an Unnormalized Form (UNF) all the way up to **4NF** to eliminate redundancy — including splitting multi-valued fields like `ItemsOrdered` and multiple customer phone numbers into their own related tables.

**Core entities:** `Users`, `Customer`, `Waiter`, `Chef`, `Manager`, `Menu`, `Orders`, `Reservation`, `Feedback`, `Sales and Stock`

Full requirements analysis, normalization steps, ER diagrams, and test cases are documented in [`Restaurant_App_Full_Documentation.pdf`](Restaurant_App_Full_Documentation.pdf).

---

## Security and Compliance

- Role-based access control (RBAC) restricts each dashboard to its intended user role
- Passwords hashed via Firebase Authentication (Android) and BCrypt (Desktop) — never stored in plain text
- All data transmitted over HTTPS/TLS
- Sensitive desktop-side data additionally encrypted with AES
- All transactions logged with timestamps for audit purposes
- Designed to align with POPIA (Protection of Personal Information Act, South Africa)

---

## Non-Functional Highlights

- Menu data loads in under 2 seconds on both platforms
- Chef dashboard receives new orders within 5 seconds of submission
- Order status syncs across devices instantly via Firebase push updates
- Built to scale to 100+ concurrent users and 10,000+ records without noticeable performance loss
- Android app supports limited offline functionality with sync-on-reconnect

---

## Getting Started

> Update this section with your actual setup steps/commands before publishing.

### Android
1. Clone the repository
2. Open the project in Android Studio
3. Add your own `google-services.json` (Firebase config) to the `app/` module
4. Build and run on an emulator (API 29+) or physical device

### Desktop
1. Ensure Java 8+ is installed
2. Set up a local MySQL database using the provided schema
3. Update database connection credentials in the config file
4. Run the packaged `.jar`, or build from source via your IDE

---

## Documentation

Full project documentation, including the problem statement, requirements analysis, ER diagrams, normalization steps, test cases, and deployment evaluation, is available in this repo: [`Restaurant_App_Full_Documentation.pdf`](./Restaurant_App_Full_Documentation.pdf).

---

## Author

**Menzi Sigwebela**
Diploma in ICT Application Development — Sol Plaatje University
[GitHub: menzi-dev](https://github.com/menzi-dev)
