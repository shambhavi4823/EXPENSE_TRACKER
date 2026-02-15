import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // DAO classes ke objects
        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        ExpenseDAO expenseDAO = new ExpenseDAO();

        while (true) {
            System.out.println("\n===== EXPENSE TRACKER MENU =====");
            System.out.println("1. Registration");
            System.out.println("2. Login");
            System.out.println("3. Add Category");
            System.out.println("4. Add Expenses");
            System.out.println("5. View Expenses");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Buffer clear karne ke liye

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();
                    System.out.print("Enter Role (Admin/User): ");
                    String role = sc.nextLine();
                    userDAO.registerUser(name, email, pass, role);
                    break;

                case 2:
                    System.out.print("Enter Email: ");
                    String loginEmail = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String loginPass = sc.nextLine();

                    boolean isLoggedIn = userDAO.loginUser(loginEmail, loginPass);

                    if (isLoggedIn) {
                        System.out.println("Aap ab system use kar sakte hain.");
                    } else {
                        System.out.println("Kripya sahi details dalein.");
                    }
                    break;

                case 3:
                    System.out.print("Enter Category Type: ");
                    String type = sc.nextLine();
                    System.out.print("Enter Item Name: ");
                    String catName = sc.nextLine();
                    System.out.print("Enter Quantity: ");
                    int catQty = sc.nextInt();
                    categoryDAO.addCategory(type, catName, catQty);
                    break;

                case 4:
                    System.out.print("Enter Expense Item: ");
                    String expName = sc.nextLine();
                    System.out.print("Enter Quantity: ");
                    int expQty = sc.nextInt();
                    System.out.print("Enter Price: ");
                    double price = sc.nextDouble();
                    expenseDAO.addExpense(expName, expQty, price);
                    break;

                case 5:
                    expenseDAO.viewExpenses();
                    break;

                case 0:
                    System.out.println("Exiting... Dhanyawad!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
}