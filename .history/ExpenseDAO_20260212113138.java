import java.sql.*;

public class ExpenseDAO {
    
    public void addExpense(String itemName, int qty, double price) {
        String query = "INSERT INTO expenses (item_type,item_name, quantity, price) VALUES (?, ?, ?,?)";

        String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
        String user = "root";
        String password = "shambhavisinha4823";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, itemName);
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

    public void viewExpenses() {
    String query = "SELECT * FROM expenses";

    String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    String user = "root";
    String password = "shambhavisinha4823";

    try (Connection con = DriverManager.getConnection(url, user, password);
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        System.out.println("\n--- Your Expenses List ---");
        System.out.println("ID | Item Name | Qty | Price | Date");
        System.out.println("------------------------------------");

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("item_name");
            int qty = rs.getInt("quantity");
            double price = rs.getDouble("price");
            String date = rs.getString("date_added");

            System.out.println(id + " | " + name + " | " + qty + " | ₹" + price + " | " + date);
        }
        System.out.println("------------------------------------\n");

    } catch (SQLException e) {
        System.out.println("Error while fetching data: " + e.getMessage());
    }
}
}