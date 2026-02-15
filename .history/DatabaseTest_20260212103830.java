import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    // Ye 'public static' hona bahut zaroori hai
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
        String user = "root";
        String password = "shambhavisinha4823";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found!");
        }
    }

    public static void main(String[] args) {
        try {
            Connection con = getConnection();
            System.out.println("Database connected successfully!");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}