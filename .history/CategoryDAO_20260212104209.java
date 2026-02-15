import java.sql.*;

public class CategoryDAO {
    
    public void addCategory(String type, String name, int qty) {
        // SQL query: values ki jagah '?' use kiya hai (PreparedStatement ke liye)
        String query = "INSERT INTO categories (item_type, item_name, quantity) VALUES (?, ?, ?)";

        // Try-with-resources: Isse connection apne aap close ho jata hai
        // Humne DatabaseTest.getConnection() call kiya hai
        try (Connection con = DatabaseTest.getConnection(); 
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // '?' ki jagah values set kar rahe hain
            pstmt.setString(1, type); 
            pstmt.setString(2, name); 
            pstmt.setInt(3, qty);    

            // Query execute karna
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("Success: Item '" + name + "' added to database!"); 
            }

        } catch (SQLException e) {
            // Agar database mein table nahi hogi ya connection fail hoga toh error dikhayega
            System.out.println("Error while adding category: " + e.getMessage());
        }
    }
}