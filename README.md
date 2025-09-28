Bookstore GUI Application

Overview

This project is a Bookstore Management System built with Java Swing (GUI) and Derby Embedded Database.
It follows an MVC-style design and demonstrates object-oriented programming, database integration, and user-friendly GUI design.

Features
• User Login / Profile
        • Sign in with name & email (auto-create account if new).
	• Update profile name.
	• Logout and re-login supported.
• Book Management
	• View all books with cover images.
	• Search books by title or author.
	• Check stock availability before adding to cart.
• Shopping Cart
	• Add, remove, and adjust quantities.
	• Validation for stock limits.
	• Checkout to create an order.
• Orders
	• View all previous orders by user.
	• Double-click to open a receipt dialog.
	• Order details include items, quantity, and total price.


Database
• Derby Embedded Database (no external server needed).
• Tables:
	• Users – stores user accounts.
	• BookProducts – stores available books.
	• Orders – stores orders.
	• OrderItems – stores items for each order.
• Read and Write operations (3+ each) are implemented across repositories.


Software Design
• OOP Principles
	• Encapsulation, Abstraction, Polymorphism applied.
• MVC Pattern
	• Model: BookProduct, Order, User
	• View: Swing GUI classes
	• Controller: CartController, UserContext
• Design Patterns
	• Singleton: DbManager
	• Repository Pattern: DbBookRepository, DbOrderRepository, DbUserRepository


Error Handling
• Invalid inputs (empty fields, wrong email, invalid quantity) → warning dialogs.
• Database exceptions → caught and displayed with error messages.
• GUI supports “Back” navigation and prevents crashes.


Testing
• JUnit test cases (5+) included:
	• CartControllerTest – add/merge items.
	• DbBookRepositoryTest – CRUD operations on books.
	• DbUserRepositoryTest – user creation & retrieval.
	• DbOrderRepositoryTest – create and retrieve orders.
• All tests validate correctness of database and business logic.


How to Run
1. Open the project in NetBeans.
2. Run App.java (main entry point).
3. Derby DB is initialized automatically (no manual setup).
4. Use provided sample data to test searching, adding to cart, and ordering.

Author
• Course Project – Bookstore GUI
• Java Swing + Derby DB
• Developed with NetBeans IDE

