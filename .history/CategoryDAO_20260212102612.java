import java.sql.*;

public class CategoryDAO {
    public void addCategory(String type, String name, int qty) {
        String query = "INSERT INTO categories (item_type, item_name, quantity) VALUES (?, ?, ?)";

        // Yahan 'DatabaseTest.getConnection()' tabhi chalega 
        // jab DatabaseTest class mein 'getConnection' static method ho.
        try (Connection con = DatabaseTest.getConnection(); 
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, type); 
            pstmt.setString(2, name); 
            pstmt.setInt(3, qty);    

            pstmt.executeUpdate();
            System.out.println("Item added to database!"); 

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}