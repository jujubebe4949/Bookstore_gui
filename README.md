# Bookstore GUI Application

## Overview
This project is a **Bookstore Management System** built with Java Swing (GUI) and an embedded Derby database.  
It follows an **MVC-style architecture**, demonstrating OOP design, database integration, and user-friendly GUI design.

---

## Features
### User Login / Profile
- Sign in with name & password
- Register with name, email, and password
- Update display name
- Secure password storage with salt & hash
- Logout and re-login supported

### Book Management
- View all books with cover images
- Search by title or author
- Check stock before adding to cart

### Shopping Cart
- Add, remove, and adjust quantities
- Validate stock limits
- Checkout to create an order

### Orders
- View previous orders by user
- Double-click to open receipt dialog
- Includes items, quantities, and total

---

## Database
**Apache Derby (Embedded, auto-created)**  
Tables:
- `Users`
- `BookProducts`
- `Orders`
- `OrderItems`

Supports **3+ Read** and **3+ Write** operations.

---

## Software Design
- **OOP Principles:** Encapsulation, Abstraction, Polymorphism  
- **MVC Pattern:**
  - Model → `BookProduct`, `Order`, `User`
  - View → Swing GUI
  - Controller → `CartController`, `UserContext`
- **Design Patterns:**
  - Singleton → `DbManager`
  - Repository → `DbBookRepository`, `DbOrderRepository`, `DbUserRepository`

---

## Error Handling
- Invalid inputs (empty fields, wrong email, invalid quantity)
- Database exceptions handled gracefully
- Back navigation and safe error recovery

---

## Testing
**JUnit 5 test cases (5+):**
- `CartControllerTest` – add/merge items
- `DbBookRepositoryTest` – CRUD on books
- `DbUserRepositoryTest` – user creation & retrieval
- `DbOrderRepositoryTest` – order creation/cancel
- `DbSmokeTest` – DB connection and schema check

---

## How to Run
```bash
ant clean jar
java -jar dist/Bookstore_gui.jar