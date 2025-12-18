# [SAMPLE] RJ Hardware and Electronics - POS and Inventory Management System

## Project Description (Prototype Only)

This project is a **sample prototype** of a POS and Inventory Management System.. It serves as a demonstration of how a Java Swing and MySQL-based application can handle retail operations.

**Note:** This is an academic/demonstration project and is not intended for use in an actual production environment. 

The primary goal of this prototype is to showcase the integration of database management, user authentication, and transaction processing in a desktop environment. 

## Features

- **Product Management**: Add, edit, and delete products with category, brand, and supplier details.
- **Inventory Management**: Track stock levels, set stock thresholds, and monitor inventory logs.
- **Sales Module**: Process transactions, calculate taxes (PHP), and manage a shopping cart.
- **Reports**: Generate detailed reports for products, sales, and inventory.
- **Category & Brand Management**: Dedicated interfaces for organizing your inventory items.
- **User Authentication**: Secure login system for authorized personnel.

## Project Structure

- `src/main/Main.java`: The entry point of the application.
- `src/ui/`: Contains all Swing-based GUI components (Frames and Dialogs).
- `src/dao/`: Data Access Objects for database interactions.
- `src/model/`: Data models (Product, Sale, Category, etc.).
- `src/db/`: Database configuration and SQL initialization scripts.
- `src/util/`: Utility classes and assets (images/icons).

## Requirements

- Java Development Kit (JDK) 8 or higher.
- MySQL Database (schema provided in `src/db/mydatabase.sql`).

## How to Run

1.  **Database Setup**:
    - Import the `src/db/mydatabase.sql` file into your MySQL server.
    - Configure the connection settings in `src/db/DatabaseConnection.java`.
2.  **Compilation**:
    - Compile the project using your preferred IDE (e.g., VS Code with Java extensions, IntelliJ, or NetBeans).
3.  **Launch**:
    - Run the `Main.java` file to start the application.

## Recent Changes
- Removed thermal printer support (NetworkPrinterManager and Arduino server) for a more streamlined checkout process.
