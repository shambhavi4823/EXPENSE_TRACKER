import java.sql.*;

public class UserDAO {
    private final String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private final String dbUser = "root";
    private final String dbPass = "shambhavisinha4823";

    public void registerUser(String name, String email, String pass, String role) {
        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, pass);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            System.out.println("✅ Registration Successful!");
        } catch (SQLException e) { System.out.println("❌ Error: " + e.getMessage()); }
    }

    public int loginUser(String email, String pass) {
        String query = "SELECT id, name FROM users WHERE email = ? AND password = ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Welcome, " + rs.getString("name"));
                return rs.getInt("id"); // User ID return kar rahe hain session ke liye
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1; // Login failed
    }

    public void updateBudget(String email, double limit) {
        String query = "UPDATE users SET monthly_budget = ? WHERE email = ?";
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setDouble(1, limit);
            pstmt.setString(2, email);
            if (pstmt.executeUpdate() > 0) System.out.println("✅ Budget Updated!");
            else System.out.println("❌ User not found!");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}