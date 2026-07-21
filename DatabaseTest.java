import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    // Ye 'public static' hona bahut zaroori hai
    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

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

}