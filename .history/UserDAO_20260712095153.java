import java.sql.*;

public class UserDAO {
    private final String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private final String dbuser = "root";
    private final String dbPass = "shambhavisinha4823";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Critical Error: MySQL JDBC Driver not found! Check your JAR file.");
        }
    }

    public void registeruser(String name, String email, String pass, String role) throws SQLException {
        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, pass);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            System.out.println("✅ User successfully registered in database!");
        }
    }

    public int loginuser(String email, String pass) {
        String query = "SELECT id, name FROM users WHERE email = ? AND password = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Login Process Success for: " + rs.getString("name"));
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("❌ Login Error: " + e.getMessage());
        }
        return -1;
    }

    public double getMonthlyBudget(int userId) {
        String query = "SELECT monthly_budget FROM users WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("monthly_budget");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading budget configuration: " + e.getMessage());
        }
        return 0.0;
    }

    public boolean updateMonthlyBudget(int userId, double newBudget) {
        String query = "UPDATE users SET monthly_budget = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setDouble(1, newBudget);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating budget profile tracking: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(int userId, String oldPass, String newPass) {
        String checkQuery = "SELECT password FROM users WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getString("password").equals(oldPass)) {
                String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newPass);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                    System.out.println("✅ Success: Password changed successfully!");
                    return true;
                }
            } else {
                System.out.println("❌ Error: Old password does not match!");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
            return false;
        }
    }

    public void viewAllUsers() {
        String query = "SELECT id, name, email, role, monthly_budget FROM users";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n-------------------------- SYSTEM USERS LIST --------------------------");
            System.out.printf("%-5s | %-15s | %-20s | %-10s | %-10s\n", "ID", "Name", "Email", "Role", "Budget");
            System.out.println("-----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-20s | %-10s | ₹%-9.2f\n", 
                                  rs.getInt("id"), rs.getString("name"), rs.getString("email"), 
                                  rs.getString("role"), rs.getDouble("monthly_budget"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching users: " + e.getMessage());
        }
    }

    public void deleteUser(String email) {
        String deleteExpenses = "DELETE FROM expenses WHERE user_id = (SELECT id FROM users WHERE email = ?)";
        String deleteUser = "DELETE FROM users WHERE email = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass)) {
            con.setAutoCommit(false);
            try (PreparedStatement pstmt1 = con.prepareStatement(deleteExpenses);
                 PreparedStatement pstmt2 = con.prepareStatement(deleteUser)) {
                pstmt1.setString(1, email);
                pstmt1.executeUpdate();
                pstmt2.setString(1, email);
                int rows = pstmt2.executeUpdate();
                if (rows > 0) {
                    con.commit();
                    System.out.println("✅ Success: User and their records deleted successfully!");
                } else {
                    con.rollback();
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("❌ Deletion failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection Error: " + e.getMessage());
        }
    }
}