import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        ExpenseDAO expenseDAO = new ExpenseDAO();

        boolean userAuthenticated = false; // Track karega ki login hua ya nahi

        while (true) {
            System.out.println("\n===== EXPENSE TRACKER MENU =====");
            if (!userAuthenticated) {
                System.out.println("1. Registration");
                System.out.println("2. Login");
            } else {
                System.out.println("3. Add Category");
                System.out.println("4. Add Expenses");
                System.out.println("5. View Expenses");
                System.out.println("6. Logout");
            }
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    if (userAuthenticated) {
                        System.out.println("Already logged in!");
                        break;
                    }
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();
                    System.out.print("Enter Role: ");
                    String role = sc.nextLine();
                    userDAO.registerUser(name, email, pass, role);
                    break;

                case 2:
                    if (userAuthenticated) {
                        System.out.println("Already logged in!");
                        break;
                    }
                    System.out.print("Enter Email: ");
                    String lEmail = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String lPass = sc.nextLine();
                    userAuthenticated = userDAO.loginUser(lEmail, lPass); // Status update hoga
                    break;

                case 3:
                    if (userAuthenticated) {
                        System.out.print("Enter Category Type: ");
                        String type = sc.nextLine();
                        System.out.print("Enter Item Name: ");
                        String catName = sc.nextLine();
                        System.out.print("Enter Quantity: ");
                        int catQty = sc.nextInt();
                        categoryDAO.addCategory(type, catName, catQty);
                    } else {
                        System.out.println("Please Login first!");
                    }
                    break;

                
               case 4:
                    if (userAuthenticated) {
                        System.out.println("\n--- Add New Expense ---");
                        
                        System.out.print("Enter Item Type (e.g., Food, Travel, Rent): ");
                        String eType = sc.nextLine();
                        
                        System.out.print("Enter Item Name: ");
                        String eName = sc.nextLine();
                        
                        System.out.print("Enter Quantity: ");
                        int eQty = sc.nextInt();
                        
                        System.out.print("Enter Price: ");
                        double ePrice = sc.nextDouble();
                        
                        // IMPORTANT: Scanner buffer clear karna taaki agla input sahi se aaye
                        sc.nextLine(); 

                        // DAO method call with 4 parameters
                        expenseDAO.addExpense(eType, eName, eQty, ePrice);
                    } else {
                        System.out.println("Error: Please Login first (Option 2)!");
                    }
                    break;

                case 5:
                    if (userAuthenticated) {
                        expenseDAO.viewExpenses();
                    } else {
                        System.out.println("Error: Please Login first (Option 2)!");
                    }
                    break;

                case 6:
                    userAuthenticated = false;
                    System.out.println("Logged out successfully!");
                    break;

                case 0:
                    System.out.println("Exiting... Dhanyawad!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}