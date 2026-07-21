public void addCategory(String itemType, String itemName, int quantity) {
    String query = "INSERT INTO categories (item_type, item_name, quantity) VALUES (?, ?, ?)";

    String url = "jdbc:mysql://localhost:3306/expense_tracker_db";
    String user = "root";
    String password = ""; // Agar password nahi hai toh khali rakhein

    try (Connection con = DriverManager.getConnection(url, user, password);
            PreparedStatement pstmt = con.prepareStatement(query)) {

        pstmt.setString(1, itemType);
        pstmt.setString(2, itemName);
        pstmt.setInt(3, quantity);

        int result = pstmt.executeUpdate();
        if (result > 0) {
            System.out.println("Category Item successfully added!");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}