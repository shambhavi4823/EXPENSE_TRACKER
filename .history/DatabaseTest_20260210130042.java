import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/expense_tracker_db;";
        String user = "root";
        String password = "shambhavisinha4823";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
