package src.db;

import java.sql.*;

public class DatabaseConnection {

    // FIELDS
    private static final String URL = "jdbc:mysql://localhost:3306/POS_AND_INVENTORY_DB";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Change this to your MySQL password

    // METHODS
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
