import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    // Ye method connection return karega
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
        String user = "root";
        String password = "shambhavisinha4823"; // Tumhara password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found!");
        }
    }

    // Is main method ko sirf testing ke liye rakho (optional)
    public static void main(String[] args) {
        try {
            Connection con = getConnection();
            if (con != null) {
                System.out.println("Database connected successfully!");
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}