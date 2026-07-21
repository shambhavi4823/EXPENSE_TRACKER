import java.sql.*;

public class UserDAO {

    private final String url = System.getenv("DB_URL");
    private final String dbuser = System.getenv("DB_USER");
    private final String dbPass = System.getenv("DB_PASSWORD");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found!");
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

            System.out.println("User Registered Successfully!");
        }
    }

    public int loginuser(String email, String pass) {

        String query = "SELECT id,name FROM users WHERE email=? AND password=?";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, pass);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    public double getMonthlyBudget(int userId) {

        String query = "SELECT monthly_budget FROM users WHERE id=?";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("monthly_budget");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0.0;
    }

    public boolean updateMonthlyBudget(int userId, double newBudget) {

        String query = "UPDATE users SET monthly_budget=? WHERE id=?";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setDouble(1, newBudget);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean changePassword(int userId, String oldPass, String newPass) {

        String checkQuery = "SELECT password FROM users WHERE id=?";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, userId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getString("password").equals(oldPass)) {

                String updateQuery = "UPDATE users SET password=? WHERE id=?";

                try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {

                    updateStmt.setString(1, newPass);
                    updateStmt.setInt(2, userId);

                    updateStmt.executeUpdate();

                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public void viewAllUsers() {

        String query = "SELECT id,name,email,role,monthly_budget FROM users";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                System.out.println(
                        rs.getInt("id") + " " +
                        rs.getString("name") + " " +
                        rs.getString("email") + " " +
                        rs.getString("role") + " " +
                        rs.getDouble("monthly_budget")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteUser(String email) {

        String deleteExpenses = "DELETE FROM expenses WHERE user_id=(SELECT id FROM users WHERE email=?)";
        String deleteUser = "DELETE FROM users WHERE email=?";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass)) {

            con.setAutoCommit(false);

            try (PreparedStatement p1 = con.prepareStatement(deleteExpenses);
                 PreparedStatement p2 = con.prepareStatement(deleteUser)) {

                p1.setString(1, email);
                p1.executeUpdate();

                p2.setString(1, email);

                int rows = p2.executeUpdate();

                if (rows > 0) {
                    con.commit();
                } else {
                    con.rollback();
                }

            } catch (SQLException e) {
                con.rollback();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getAdminAnalyticsJson() {

        int totalUsers = 0;
        double globalExpenses = 0;
        int totalQueries = 0;

        String usersQuery = "SELECT COUNT(*) FROM users";
        String expenseQuery = "SELECT SUM(quantity*price),COUNT(*) FROM expenses";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass)) {

            try (PreparedStatement ps = con.prepareStatement(usersQuery);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalUsers = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(expenseQuery);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    globalExpenses = rs.getDouble(1);
                    totalQueries = rs.getInt(2);
                }
            }

            return String.format(
                    "{\"status\":\"success\",\"totalUsers\":%d,\"globalExpenses\":%.2f,\"totalQueries\":%d}",
                    totalUsers,
                    globalExpenses,
                    totalQueries
            );

        } catch (SQLException e) {

            return "{\"status\":\"error\"}";
        }
    }

    // *********** THIS METHOD WAS MISSING ***********

    public String getAllUsersJson() {

        StringBuilder json = new StringBuilder("[");

        String query = "SELECT id,name,email,role,monthly_budget FROM users";

        try (Connection con = DriverManager.getConnection(url, dbuser, dbPass);
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            boolean first = true;

            while (rs.next()) {

                if (!first)
                    json.append(",");

                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"name\":\"").append(rs.getString("name")).append("\",")
                        .append("\"email\":\"").append(rs.getString("email")).append("\",")
                        .append("\"role\":\"").append(rs.getString("role")).append("\",")
                        .append("\"budget\":").append(rs.getDouble("monthly_budget"))
                        .append("}");

                first = false;
            }

            json.append("]");

            return json.toString();

        } catch (SQLException e) {

            e.printStackTrace();

            return "[]";
        }
    }
}