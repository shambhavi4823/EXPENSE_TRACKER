import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UserDAO userDAO = new UserDAO();
        ExpenseDAO expenseDAO = new ExpenseDAO();

        boolean userAuthenticated = false;
        int loggedInUserId = -1; // User ki ID track karne ke liye
        String userRole = "";    // Role check karne ke liye (admin/user)

        while (true) {
            System.out.println("\n===== EXPENSE TRACKER MENU =====");
            if (!userAuthenticated) {
                System.out.println("1. Registration");
                System.out.println("2. Login");
            } else {
                System.out.println("3. Add Expenses");
                System.out.println("4. View My Expenses");
                // Sirf Admin ko hi limit set karne ka option dikhega
                if (userRole.equalsIgnoreCase("admin")) {
                    System.out.println("5. Set User Monthly Budget (Admin Only)");
                }
                System.out.println("6. Logout");
            }
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Buffer clear

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
                    System.out.print("Enter Role (admin/user): ");
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
                    
                    // UserDAO ka login method ab ID return karega (Update this in UserDAO)
                    loggedInUserId = userDAO.loginUser(lEmail, lPass); 
                    
                    if (loggedInUserId != -1) {
                        userAuthenticated = true;
                        // Is step par hum user ka role bhi set kar sakte hain
                        // Filhaal demo ke liye hum userRole ko "admin" ya "user" assume kar rahe hain
                        // Aap login logic mein role bhi fetch kar sakte hain
                        userRole = lEmail.contains("admin") ? "admin" : "user"; 
                    }
                    break;

                case 3:
                    if (userAuthenticated) {
                        System.out.println("\n--- Add New Expense ---");
                        System.out.print("Enter Item Type (e.g., Food, Travel): ");
                        String eType = sc.nextLine();
                        System.out.print("Enter Item Name: ");
                        String eName = sc.nextLine();
                        System.out.print("Enter Quantity: ");
                        int eQty = sc.nextInt();
                        System.out.print("Enter Price: ");
                        double ePrice = sc.nextDouble();
                        sc.nextLine(); 

                        // IMPORTANT: Ab hum loggedInUserId bhi bhej rahe hain validation ke liye
                        expenseDAO.addExpense(loggedInUserId, eType, eName, eQty, ePrice);
                    } else {
                        System.out.println("Error: Please Login first!");
                    }
                    break;

                case 4:
                    if (userAuthenticated) {
                        expenseDAO.viewExpenses(loggedInUserId);
                    } else {
                        System.out.println("Error: Please Login first!");
                    }
                    break;

                case 5:
                    if (userAuthenticated && userRole.equalsIgnoreCase("admin")) {
                        System.out.print("Enter User Email to set budget: ");
                        String targetEmail = sc.nextLine();
                        System.out.print("Enter Monthly Budget Limit: ");
                        double limit = sc.nextDouble();
                        sc.nextLine();
                        userDAO.updateMonthlyBudget(targetEmail, limit);
                    } else {
                        System.out.println("Access Denied: Admin only feature!");
                    }
                    break;

                case 6:
                    userAuthenticated = false;
                    loggedInUserId = -1;
                    userRole = "";
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