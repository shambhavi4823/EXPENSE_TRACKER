import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        // 1. Connection details
        // Syntax: jdbc:mysql://[host]:[port]/[databaseName]
        String url = "jdbc:mysql://localhost:3306/expense_tracker"; 
        String user = "root";
        String password = "shambhavisinha4823"; // Put your actual password

        System.out.println("Connecting to database...");

        try {
            // 2. Establish connection
            Connection conn = DriverManager.getConnection(url, user, password);
            
            if (conn != null) {
                System.out.println("Success! Connected to MySQL Workbench.");
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Connection failed! Check your password or if MySQL is running.");
            e.printStackTrace();
        }
    }
}