// File: CategoryDAO.java
import java.sql.*;

public class CategoryDAO {
    public void addCategory(String type, String name, int qty) {
        String query = "INSERT INTO categories (item_type, item_name, quantity) VALUES (?, ?, ?)";

        // Try-with-resources: Ye connection ko apne aap close kar dega
        try (Connection con = DatabaseTest.getConnection(); 
     PreparedStatement pstmt = con.prepareStatement(query)) {

    pstmt.setString(1, type); // Direct '1' likho
    pstmt.setString(2, name); // Direct '2' likho
    pstmt.setInt(3, qty);    // Direct '3' likho

    pstmt.executeUpdate();
    System.out.println("Item added to database!"); // 'x:' hata diya

} catch (SQLException e) {
    System.out.println("Error: " + e.getMessage());
}
    }
}