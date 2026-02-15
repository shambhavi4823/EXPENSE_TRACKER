import java.sql.*;

public class ExpenseDAO {
    
    private final String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private final String user = "root";
    private final String password = "shambhavisinha4823";

    // Ab hum userId bhi pass karenge taaki check kar sakein ki US USER ka budget kya hai
    public void addExpense(int userId, String itemType, String itemName, int qty, double price) {
        
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            
            // --- 1. BUDGET CHECK LOGIC ---
            
            // Pehle user ka monthly limit fetch karo
            String budgetQuery = "SELECT monthly_budget FROM users WHERE id = ?";
            PreparedStatement budgetStmt = con.prepareStatement(budgetQuery);
            budgetStmt.setInt(1, userId);
            ResultSet rsBudget = budgetStmt.executeQuery();
            
            double monthlyLimit = 0;
            if (rsBudget.next()) {
                monthlyLimit = rsBudget.getDouble("monthly_budget");
            }

            // Phir is mahine ka total kharcha nikaalo
            String totalSpentQuery = "SELECT SUM(price) FROM expenses WHERE user_id = ? AND MONTH(date_added) = MONTH(CURRENT_DATE())";
            PreparedStatement spentStmt = con.prepareStatement(totalSpentQuery);
            spentStmt.setInt(1, userId);
            ResultSet rsSpent = spentStmt.executeQuery();
            
            double currentSpent = 0;
            if (rsSpent.next()) {
                currentSpent = rsSpent.getDouble(1);
            }

            // Agar limit set hai (limit > 0) aur naya kharcha limit cross kar raha hai
            if (monthlyLimit > 0 && (currentSpent + price) > monthlyLimit) {
                System.out.println("\n❌ ERROR: Budget Limit Exceeded!");
                System.out.println("Your Monthly Limit: ₹" + monthlyLimit);
                System.out.println("Already Spent: ₹" + currentSpent);
                System.out.println("This transaction would put you over by: ₹" + ((currentSpent + price) - monthlyLimit));
                return; // Method yahin se bahar nikal jayega, expense insert nahi hoga
            }

            // --- 2. INSERT EXPENSE (Agar budget allow kare) ---
            
            String insertQuery = "INSERT INTO expenses (user_id, item_type, item_name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, itemType);
                pstmt.setString(3, itemName);
                pstmt.setInt(4, qty);
                pstmt.setDouble(5, price);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ Success: Expense added!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewExpenses(int userId) {
        // Sirf wahi expenses dikhao jo login user ke hain
        String query = "SELECT * FROM expenses WHERE user_id = ?";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n----------------------- YOUR EXPENSES LIST -----------------------");
            System.out.printf("%-5s | %-15s | %-15s | %-5s | %-10s | %-15s\n", 
                              "ID", "Type", "Item Name", "Qty", "Price", "Date");
            System.out.println("------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-15s | %-5d | ₹%-9.2f | %-15s\n", 
                                  rs.getInt("id"), rs.getString("item_type"), rs.getString("item_name"), 
                                  rs.getInt("quantity"), rs.getDouble("price"), rs.getString("date_added"));
            }
            System.out.println("------------------------------------------------------------------\n");

        } catch (SQLException e) {
            System.out.println("Error while fetching data: " + e.getMessage());
        }
    }
}