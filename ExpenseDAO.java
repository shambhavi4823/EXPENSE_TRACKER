import java.sql.*;

public class ExpenseDAO {
    private final String url = System.getenv("DB_URL");
    private final String user = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASSWORD");

    public void updateMonthlyBudget(int userId, double newLimit) {
        String query = "UPDATE users SET monthly_budget = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setDouble(1, newLimit);
            pstmt.setInt(2, userId);
            if (pstmt.executeUpdate() > 0) {
                System.out.println("✅ Success: Monthly budget updated to ₹" + newLimit);
            }
        } catch (SQLException e) {
            System.out.println("Error updating budget: " + e.getMessage());
        }
    }

    public String addExpense(int userId, String itemType, String itemName, int qty, double price) {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String budgetQuery = "SELECT monthly_budget FROM users WHERE id = ?";
            double monthlyLimit = 10000.00;
            try (PreparedStatement bStmt = con.prepareStatement(budgetQuery)) {
                bStmt.setInt(1, userId);
                try (ResultSet rsBudget = bStmt.executeQuery()) {
                    if (rsBudget.next() && rsBudget.getDouble("monthly_budget") > 0) {
                        monthlyLimit = rsBudget.getDouble("monthly_budget");
                    }
                }
            }

            String spentQuery = "SELECT SUM(price * quantity) FROM expenses WHERE user_id = ? AND MONTH(date_added) = MONTH(CURRENT_DATE()) AND YEAR(date_added) = YEAR(CURRENT_DATE())";
            double currentSpent = 0;
            try (PreparedStatement spentStmt = con.prepareStatement(spentQuery)) {
                spentStmt.setInt(1, userId);
                try (ResultSet rsSpent = spentStmt.executeQuery()) {
                    if (rsSpent.next())
                        currentSpent = rsSpent.getDouble(1);
                }
            }

            double totalProposed = price * qty;
            if ((currentSpent + totalProposed) > monthlyLimit) {
                System.out.println("\n❌ ERROR: Monthly Budget Exceeded! Limit: ₹" + monthlyLimit);
                return "{\"status\":\"limit_exceeded\",\"budget\":" + monthlyLimit + ",\"spent\":" + currentSpent + "}";
            }

            String insertQuery = "INSERT INTO expenses (user_id, item_type, item_name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, itemType);
                pstmt.setString(3, itemName);
                pstmt.setInt(4, qty);
                pstmt.setDouble(5, price);
                pstmt.executeUpdate();
                System.out.println("✅ Success: Expense added!");
                return "{\"status\":\"success\"}";
            }
        } catch (SQLException e) {
            System.out.println("Error adding expense: " + e.getMessage());
            return "{\"status\":\"error\"}";
        }
    }

    public String getDashboardData(int userId) {
        double budgetLimit = 10000.00;
        double totalSpent = 0.0;
        StringBuilder recentExpensesJson = new StringBuilder();

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String userQuery = "SELECT monthly_budget FROM users WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(userQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getDouble("monthly_budget") > 0)
                        budgetLimit = rs.getDouble("monthly_budget");
                }
            }

            String spentQuery = "SELECT SUM(price * quantity) FROM expenses WHERE user_id = ? AND MONTH(date_added) = MONTH(CURRENT_DATE()) AND YEAR(date_added) = YEAR(CURRENT_DATE())";
            try (PreparedStatement pstmt = con.prepareStatement(spentQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next())
                        totalSpent = rs.getDouble(1);
                }
            }

            String recentQuery = "SELECT id, item_type, item_name, quantity, price, date_added FROM expenses WHERE user_id = ? ORDER BY date_added DESC LIMIT 5";
            try (PreparedStatement pstmt = con.prepareStatement(recentQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        if (recentExpensesJson.length() > 0)
                            recentExpensesJson.append(",");
                        recentExpensesJson.append(String.format(
                                "{\"id\":%d,\"type\":\"%s\",\"name\":\"%s\",\"qty\":%d,\"price\":%.2f,\"date\":\"%s\"}",
                                rs.getInt("id"), rs.getString("item_type"), rs.getString("item_name"),
                                rs.getInt("quantity"), rs.getDouble("price"), rs.getString("date_added")));
                    }
                }
            }
        } catch (SQLException e) {
            return "{\"status\":\"error\"}";
        }

        double remaining = budgetLimit - totalSpent;
        double consumptionPercent = budgetLimit > 0 ? (totalSpent / budgetLimit) * 100 : 0;
        if (consumptionPercent > 100)
            consumptionPercent = 100;

        return String.format(
                "{\"status\":\"success\",\"budget\":%.2f,\"spent\":%.2f,\"remaining\":%.2f,\"percent\":%.2f,\"expenses\":[%s]}",
                budgetLimit, totalSpent, remaining, consumptionPercent, recentExpensesJson.toString());
    }

    public void viewExpenses(int userId) {
        String query = "SELECT * FROM expenses WHERE user_id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n----------------------- YOUR EXPENSES LIST -----------------------");
            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-15s | %-5d | ₹%-9.2f | %-15s\n",
                        rs.getInt("id"), rs.getString("item_type"), rs.getString("item_name"),
                        rs.getInt("quantity"), rs.getDouble("price"), rs.getString("date_added"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }

    public String getUserExpensesJson(int userId) {
        StringBuilder json = new StringBuilder();
        String query = "SELECT id, item_type, item_name, quantity, price, date_added FROM expenses WHERE user_id = ? ORDER BY date_added DESC";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (json.length() > 0)
                        json.append(",");
                    json.append(String.format(
                            "{\"id\":%d,\"type\":\"%s\",\"name\":\"%s\",\"qty\":%d,\"price\":%.2f,\"date\":\"%s\"}",
                            rs.getInt("id"), rs.getString("item_type"), rs.getString("item_name"),
                            rs.getInt("quantity"), rs.getDouble("price"), rs.getString("date_added")));
                }
            }
        } catch (SQLException e) {
            return "[]";
        }
        return "[" + json.toString() + "]";
    }

    public void deleteExpense(int userId, int expenseId) {
        String query = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);
            if (pstmt.executeUpdate() > 0)
                System.out.println("✅ Expense deleted successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }

    public void editExpense(int userId, int expenseId, String newType, String newName, int newQty, double newPrice) {
        String query = "UPDATE expenses SET item_type = ?, item_name = ?, quantity = ?, price = ? WHERE id = ? AND user_id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, newType);
            pstmt.setString(2, newName);
            pstmt.setInt(3, newQty);
            pstmt.setDouble(4, newPrice);
            pstmt.setInt(5, expenseId);
            pstmt.setInt(6, userId);
            if (pstmt.executeUpdate() > 0)
                System.out.println("✅ Expense updated successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }

    // UPDATED FUNCTION: Returns structured JSON instead of local terminal stdout
    // prints
    public String searchExpensesJson(int userId, String searchType, String keyword) {
        StringBuilder json = new StringBuilder();
        String query = searchType.equalsIgnoreCase("category")
                ? "SELECT id, item_type, item_name, quantity, price, date_added FROM expenses WHERE user_id = ? AND item_type LIKE ?"
                : "SELECT id, item_type, item_name, quantity, price, date_added FROM expenses WHERE user_id = ? AND DATE(date_added) = ?";

        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, searchType.equalsIgnoreCase("category") ? "%" + keyword + "%" : keyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (json.length() > 0)
                        json.append(",");
                    json.append(String.format(
                            "{\"id\":%d,\"type\":\"%s\",\"name\":\"%s\",\"qty\":%d,\"price\":%.2f,\"date\":\"%s\"}",
                            rs.getInt("id"), rs.getString("item_type"), rs.getString("item_name"),
                            rs.getInt("quantity"), rs.getDouble("price"), rs.getString("date_added")));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Search Query Exception: " + e.getMessage());
            return "[]";
        }
        return "[" + json.toString() + "]";
    }

    public void searchExpenses(int userId, String searchType, String keyword) {
    }

    public void adminDeleteExpense(int expenseId) {
        String query = "DELETE FROM expenses WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, expenseId);
            if (pstmt.executeUpdate() > 0)
                System.out.println("✅ Success (Admin): Expense ID removed!");
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }

    public void adminSearchExpenses(String category) {
        String query = "SELECT e.id, u.name, e.item_type, e.item_name, e.quantity, e.price, e.date_added FROM expenses e JOIN users u ON e.user_id = u.id WHERE e.item_type LIKE ? ORDER BY e.date_added DESC";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, "%" + category + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-12s | %-15s | %-5d | ₹%-9.2f | %-15s\n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("item_type"),
                        rs.getString("item_name"), rs.getInt("quantity"), rs.getDouble("price"),
                        rs.getString("date_added"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }

    public void adminViewBudgetHistory() {
        System.out.println("❌ History snapshot clean. No logs initialized yet.");
    }

    public void adminViewParticularUserExpenses(String userEmail) {
        String query = "SELECT e.id, u.name, e.item_type, e.item_name, e.quantity, e.price, e.date_added FROM expenses e JOIN users u ON e.user_id = u.id WHERE u.email = ? ORDER BY e.date_added DESC";
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-15s | %-5d | ₹%-9.2f | %-15s\n",
                        rs.getInt("id"), rs.getString("item_type"), rs.getString("item_name"),
                        rs.getInt("quantity"), rs.getDouble("price"), rs.getString("date_added"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }
}