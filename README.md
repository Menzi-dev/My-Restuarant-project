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

## App Walkthrough

### Onboarding and Authentication

The app opens with a branded splash screen, then routes users into a role-aware login/registration flow — the same account type (Customer, Waiter, Chef, Manager) determines which dashboard they land on.

| Splash Screen | Login | Register |
|---|---|---|
| ![Splash Screen](screenshots/01-splash-screen.png) | ![Login](screenshots/02-login.png) | ![Register](screenshots/03-register.png) |

---

### Customer Experience

**Browsing the menu**

Customers pick a category (Breakfast, Lunch, Dinner, Drinks) and browse items with images and prices before adding them to their cart.

| Menu Categories | Menu Items |
|---|---|
| ![Menu Category](screenshots/04-menu-category.png) | ![Menu Items](screenshots/05-menu-items.png) |

**Cart and Checkout**

Selected items land in a running cart with a live total, ready for checkout.

<img src="screenshots/06-cart.png" width="320" alt="Cart"/>

**Table Reservations**

Customers select a date, time, and guest count, then choose from available tables (each labeled with its view and seating capacity) and confirm the booking.

| Select Table | Reservation Details | Confirm |
|---|---|---|
| ![Table Reservation](screenshots/07-table-reservation.png) | ![Reservation Select](screenshots/08-table-reservation-select.png) | ![Reservation Confirm](screenshots/09-reservation-confirm.png) |

**Confirmation, Feedback and Tracking**

After checkout, customers get a confirmation screen with direct links to track their order/reservation or leave feedback.

| Thank You | Feedback | Track Order (Submitted) |
|---|---|---|
| ![Thank You](screenshots/10-thank-you.png) | ![Feedback](screenshots/11-feedback.png) | ![Track Order Submitted](screenshots/12-track-order-submitted.png) |

Order status updates in real time as it moves through the kitchen and out to the table:

<img src="screenshots/16-track-order-delivered.png" width="320" alt="Order Delivered"/>

---

### Waiter Dashboard

Waiters see incoming orders that need to be accepted or declined, and a separate queue of ready orders awaiting delivery.

| Orders to Process | Ready to Deliver |
|---|---|
| ![Waiter Orders to Process](screenshots/13-waiter-orders-to-process.png) | ![Waiter Ready to Deliver](screenshots/15-waiter-ready-to-deliver.png) |

---

### Chef Dashboard

Once a waiter accepts an order, it lands on the chef's queue. Chefs move each order from Order Accepted, to Being Prepared, to Ready, notifying the waiter and customer automatically.

<img src="screenshots/14-chef-dashboard.png" width="320" alt="Chef Dashboard"/>

---

### Manager Dashboard

The manager gets a live overview of total users, income, and reservations, plus three control panels:

- **Manage Menu** — add new items with name, price, and category
- **Pending Reservations** — approve or decline incoming bookings
- **Sales and Stock** — monitor item-level availability at a glance

| Pending Reservations | Manage Menu | Sales and Stock |
|---|---|---|
| ![Manager Reservations](screenshots/17-manager-reservations.png) | ![Manage Menu](screenshots/18-manager-manage-menu.png) | ![Sales and Stock](screenshots/19-manager-sales-stock.png) |

---

## System Design

The system was designed around four core stakeholders (Customer, Waiter, Chef, Manager) interacting with a shared backend through a Context Diagram and Use Case Diagram, and a relational schema normalized from an Unnormalized Form (UNF) all the way up to **4NF** to eliminate redundancy — including splitting multi-valued fields like `ItemsOrdered` and multiple customer phone numbers into their own related tables.

**Core entities:** `Users`, `Customer`, `Waiter`, `Chef`, `Manager`, `Menu`, `Orders`, `Reservation`, `Feedback`, `Sales and Stock`

Full requirements analysis, normalization steps, ER diagrams, and test cases are documented in [`Restaurant_App_Full_Documentation.pdf`](./Restaurant_App_Full_Documentation.pdf).

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
