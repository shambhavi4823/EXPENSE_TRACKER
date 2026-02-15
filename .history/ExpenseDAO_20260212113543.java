import java.sql.*;

public class ExpenseDAO {
    
    // Database credentials (Common for both methods)
    private final String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private final String user = "root";
    private final String password = "shambhavisinha4823";

    public void addExpense(String itemType, String itemName, int qty, double price) {
        String query = "INSERT INTO expenses (item_type, item_name, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, itemType);
            pstmt.setString(2, itemName);
            pstmt.setInt(3, qty);
            pstmt.setDouble(4, price);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Success: Expense added [" + itemType + "] " + itemName + " | ₹" + price);
            }

        } catch (SQLException e) {
            System.out.println("Error while adding expense: " + e.getMessage());
        }
    }

    public void viewExpenses() {
        String query = "SELECT * FROM expenses";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n----------------------- YOUR EXPENSES LIST -----------------------");
            System.out.printf("%-5s | %-15s | %-15s | %-5s | %-10s | %-15s\n", 
                              "ID", "Type", "Item Name", "Qty", "Price", "Date");
            System.out.println("------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("item_type"); // Naya Column
                String name = rs.getString("item_name");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String date = rs.getString("date_added");

                System.out.printf("%-5d | %-15s | %-15s | %-5d | ₹%-9.2f | %-15s\n", 
                                  id, type, name, qty, price, date);
            }
            System.out.println("------------------------------------------------------------------\n");

        } catch (SQLException e) {
            System.out.println("Error while fetching data: " + e.getMessage());
        }
    }
}