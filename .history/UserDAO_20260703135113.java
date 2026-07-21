import java.sql.*;

public class UserDAO {
    private final String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private final String dbuser = "root";
    private final String dbPass = "shambhavisinha4823";

    // 1. Static Block: Yeh driver ko class load hote hi register kar dega
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Critical Error: MySQL JDBC Driver not found! Check your JAR file.");
        }
    }

    // --- REGISTER METHOD ---
    public void registeruser(String name, String email, String pass, String role) {
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            System.out.println("❌ Error: Email must be in @gmail.com format!");
            return;
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*]).{6,}$";
        if (!pass.matches(passwordRegex)) {
            System.out.println("❌ Error: Password is too weak!");
            return;
        }

        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, pass);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            System.out.println("✅ User successfully registered!");
        } catch (SQLException e) {
            System.out.println("❌ Registration Error: " + e.getMessage());
        }
    }

    // --- LOGIN METHOD ---
    public int loginuser(String email, String pass) {
        String query = "SELECT id, name FROM users WHERE email = ? AND password = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Login Successful! Welcome, " + rs.getString("name"));
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("❌ Login Error: " + e.getMessage());
        }
        return -1;
    }

    // --- VIEW ALL USERS (ADMIN ONLY) ---
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
                                  rs.getInt("id"), 
                                  rs.getString("name"), 
                                  rs.getString("email"), 
                                  rs.getString("role"),
                                  rs.getDouble("monthly_budget"));
            }
            System.out.println("-----------------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching users: " + e.getMessage());
        }
    }

    // --- DELETE USER (ADMIN ONLY) ---
    public void deleteUser(String email) {
        // Query to delete associated expenses first to avoid Foreign Key errors
        String deleteExpenses = "DELETE FROM expenses WHERE user_id = (SELECT id FROM users WHERE email = ?)";
        String deleteUser = "DELETE FROM users WHERE email = ?";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass)) {
            con.setAutoCommit(false); // Transaction management
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
                    System.out.println("❌ Error: User with email " + email + " not found!");
                    con.rollback();
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("❌ Error during deletion: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection Error: " + e.getMessage());
        }
    }

    // --- CHANGE PASSWORD ---
    public void changePassword(int userId, String oldPass, String newPass) {
        String checkQuery = "SELECT password FROM users WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass)) {
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                if (!rs.getString("password").equals(oldPass)) {
                    System.out.println("❌ Error: Old password does not match!");
                    return;
                }
            }

            String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*]).{6,}$";
            if (!newPass.matches(passwordRegex)) {
                System.out.println("❌ Error: New password is too weak!");
                return;
            }

            String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setString(1, newPass);
            updateStmt.setInt(2, userId);
            updateStmt.executeUpdate();
            System.out.println("✅ Success: Password changed successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }
}