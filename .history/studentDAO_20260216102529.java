import java.sql.*;

public class studentDAO {
    private final String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private final String dbstudent = "root";
    private final String dbPass = "shambhavisinha4823";

    public void registerstudent(String name, String email, String pass, String role) {
        String query = "INSERT INTO students (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, dbstudent, dbPass);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, pass);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            System.out.println("✅ student successfully registered!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Is method ko dhyan se dekhiye, iska naam wahi hai jo error mein missing tha
    public void updateMonthlyBudget(String email, double limit) {
        String query = "UPDATE students SET monthly_budget = ? WHERE email = ?";
        try (Connection con = DriverManager.getConnection(url, dbstudent, dbPass);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setDouble(1, limit);
            pstmt.setString(2, email);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Budget updated for " + email + " to ₹" + limit);
            } else {
                System.out.println("❌ student not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating budget: " + e.getMessage());
        }
    }

    public int loginstudent(String email, String pass) {
        String query = "SELECT id, name FROM students WHERE email = ? AND password = ?";
        try (Connection con = DriverManager.getConnection(url, dbstudent, dbPass);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Login Successful! Welcome, " + rs.getString("name"));
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}