import java.sql.*;

public class ExpenseDAO {
    
    public void addExpense(String itemName, int qty, double price) {
        String query = "INSERT INTO expenses (item_name, quantity, price) VALUES (?, ?, ?)";

        String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
        String user = "root";
        String password = "shambhavisinha4823";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, itemName);
            pstmt.setInt(2, qty);
            pstmt.setDouble(3, price);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Expense added: " + itemName + " | ₹" + price);
            }

        } catch (SQLException e) {
            System.out.println("Error while adding expense: " + e.getMessage());
        }
    }
}