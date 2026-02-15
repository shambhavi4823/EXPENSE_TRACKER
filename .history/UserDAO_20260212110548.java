import java.sql.*;

public class UserDAO {
    // Ye method frontend se data lekar database me save karega
    public void registerUser(String name, String email, String pass, String role) {
        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        
        String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
        String user = "root";
        String password = "shambhavisinha4823"; // Apna password check kar lena

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Value set kar rahe hain (?) ki jagah
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, pass);
            pstmt.setString(4, role);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User successfully registered in Backend!");
            }


            

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}