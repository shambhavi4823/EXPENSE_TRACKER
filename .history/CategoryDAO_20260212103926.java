import java.sql.*;

public class CategoryDAO {
    public void addCategory(String type, String name, int qty) {
        String query = "INSERT INTO categories (item_type, item_name, quantity) VALUES (?, ?, ?)";

        // Yahan ab error nahi aayega kyunki humne DatabaseTest mein method bana diya
        // hai
        try (Connection con = DatabaseTest.getConnection(url, user, password);
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