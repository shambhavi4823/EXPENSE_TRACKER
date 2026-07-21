import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class main {
   public main() {
   }

   public static void main(String[] var0) {
      try {
         HttpServer var1 = HttpServer.create(new InetSocketAddress(8080), 0);
         
         // Binding Core Auth & User Paths
         var1.createContext("/api/register", new RegisterHandler());
         var1.createContext("/api/login", new LoginHandler());
         var1.createContext("/api/user-dashboard", new UserDashboardHandler());
         var1.createContext("/api/add-expense", new AddExpenseHandler());
         var1.createContext("/api/search-expenses", new SearchExpensesHandler());
         var1.createContext("/api/get-budget", new GetBudgetHandler());
         var1.createContext("/api/update-budget", new UpdateBudgetHandler());
         var1.createContext("/api/change-password", new ChangePasswordHandler());
         
         // Expense History Management API Endpoints
         var1.createContext("/api/get-expenses", new GetExpensesHandler());
         var1.createContext("/api/delete-expense", new DeleteExpenseHandler());
         var1.createContext("/api/edit-expense", new EditExpenseHandler());

         // --- ADMIN PANEL API ENDPOINTS ---
         var1.createContext("/api/admin-analytics", new AdminAnalyticsHandler());
         var1.createContext("/api/admin-users", new AdminUsersHandler());
         var1.createContext("/api/admin-delete-user", new AdminDeleteUserHandler());
         
         var1.setExecutor((Executor)null);
         System.out.println("====================================================");
         System.out.println("🚀 WalletWise Active Web Connection Engine Running!");
         System.out.println("🔗 Backend Target Gateway Address: http://localhost:8080");
         System.out.println("====================================================");
         var1.start();
      } catch (IOException var2) {
         System.out.println("❌ Server Initiation Fault: " + var2.getMessage());
      }
   }

   public static Map<String, String> parseFormData(String var0) {
      HashMap<String, String> var1 = new HashMap<>();
      if (var0 != null && !var0.isEmpty()) {
         String[] var2 = var0.split("&");

         for(String var6 : var2) {
            String[] var7 = var6.split("=");
            if (var7.length > 0) {
               String var8 = URLDecoder.decode(var7[0], StandardCharsets.UTF_8);
               String var9 = var7.length > 1 ? URLDecoder.decode(var7[1], StandardCharsets.UTF_8) : "";
               var1.put(var8, var9);
            }
         }
         return var1;
      } else {
         return var1;
      }
   }

   private static void configureCorsHeaders(HttpExchange var0) {
      var0.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
      var0.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
      var0.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
   }

   // --- ADMIN PANEL HANDLERS ---

   static class AdminAnalyticsHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else {
            String response = (new UserDAO()).getAdminAnalyticsJson();
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream os = var1.getResponseBody();
            os.write(responseBytes);
            os.close();
         }
      }
   }

   static class AdminUsersHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else {
            String response = (new UserDAO()).getAllUsersJson();
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream os = var1.getResponseBody();
            os.write(responseBytes);
            os.close();
         }
      }
   }

   static class AdminDeleteUserHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            String email = var3.get("email");
            
            (new UserDAO()).deleteUser(email);
            String response = "{\"status\":\"success\"}";
            
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream os = var1.getResponseBody();
            os.write(responseBytes);
            os.close();
         }
      }
   }

   // --- EXPENSE HISTORY HANDLERS ---

   static class GetExpensesHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            
            String var5 = (new ExpenseDAO()).getUserExpensesJson(var4);
            byte[] responseBytes = var5.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var6 = var1.getResponseBody();
            var6.write(responseBytes);
            var6.close();
         }
      }
   }

   static class DeleteExpenseHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            int var5 = Integer.parseInt(var3.get("expenseId"));
            
            (new ExpenseDAO()).deleteExpense(var4, var5);
            String var6 = "{\"status\":\"success\"}";
            
            byte[] responseBytes = var6.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var7 = var1.getResponseBody();
            var7.write(responseBytes);
            var7.close();
         }
      }
   }

   static class EditExpenseHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            
            int userId = Integer.parseInt(var3.get("userId"));
            int expenseId = Integer.parseInt(var3.get("expenseId"));
            String type = var3.get("item_type");
            String name = var3.get("item_name");
            int qty = Integer.parseInt(var3.get("quantity"));
            double price = Double.parseDouble(var3.get("price"));
            
            (new ExpenseDAO()).editExpense(userId, expenseId, type, name, qty, price);
            String response = "{\"status\":\"success\"}";
            
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream os = var1.getResponseBody();
            os.write(responseBytes);
            os.close();
         }
      }
   }

   // --- PRE-EXISTING AUTH & DASHBOARD HANDLERS ---

   static class RegisterHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            String var4;
            try {
               (new UserDAO()).registeruser(var3.get("name"), var3.get("email"), var3.get("password"), var3.get("role"));
               var4 = "{\"status\":\"success\"}";
               byte[] responseBytes = var4.getBytes(StandardCharsets.UTF_8);
               var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
               var1.sendResponseHeaders(200, (long)responseBytes.length);
            } catch (Exception var6) {
               var4 = "{\"status\":\"error\",\"message\":\"Registration Error Exception.\"}";
               byte[] responseBytes = var4.getBytes(StandardCharsets.UTF_8);
               var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
               var1.sendResponseHeaders(400, (long)responseBytes.length);
            }
            OutputStream var5 = var1.getResponseBody();
            var5.write(var4.getBytes(StandardCharsets.UTF_8));
            var5.close();
         }
      }
   }

   static class LoginHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = (new UserDAO()).loginuser(var3.get("email"), var3.get("password"));
            String var5;
            if (var4 == -1) {
               var5 = "{\"status\":\"error\",\"message\":\"Invalid details provided!\"}";
               byte[] responseBytes = var5.getBytes(StandardCharsets.UTF_8);
               var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
               var1.sendResponseHeaders(401, (long)responseBytes.length);
            } else {
               String var6 = !var3.get("email").toLowerCase().contains("admin") && !var3.get("email").equalsIgnoreCase("tushar@gmail.com") ? "user" : "admin";
               var5 = "{\"status\":\"success\",\"userId\":" + var4 + ",\"role\":\"" + var6 + "\",\"email\":\"" + var3.get("email") + "\"}";
               byte[] responseBytes = var5.getBytes(StandardCharsets.UTF_8);
               var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
               var1.sendResponseHeaders(200, (long)responseBytes.length);
            }
            OutputStream var7 = var1.getResponseBody();
            var7.write(var5.getBytes(StandardCharsets.UTF_8));
            var7.close();
         }
      }
   }

   static class UserDashboardHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            String var5 = (new ExpenseDAO()).getDashboardData(var4);
            
            byte[] responseBytes = var5.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var6 = var1.getResponseBody();
            var6.write(responseBytes);
            var6.close();
         }
      }
   }

   static class AddExpenseHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            String var5 = var3.get("item_type");
            String var6 = var3.get("item_name");
            int var7 = Integer.parseInt(var3.get("quantity"));
            double var8 = Double.parseDouble(var3.get("price"));
            String var10 = (new ExpenseDAO()).addExpense(var4, var5, var6, var7, var8);
            
            byte[] responseBytes = var10.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var11 = var1.getResponseBody();
            var11.write(responseBytes);
            var11.close();
         }
      }
   }

   static class SearchExpensesHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            String var5 = var3.get("searchType");
            String var6 = var3.get("keyword");
            String var7 = (new ExpenseDAO()).searchExpensesJson(var4, var5, var6);
            
            byte[] responseBytes = var7.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var8 = var1.getResponseBody();
            var8.write(responseBytes);
            var8.close();
         }
      }
   }

   static class GetBudgetHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            double budget = (new UserDAO()).getMonthlyBudget(var4);
            String var5 = "{\"status\":\"success\",\"budget\":" + budget + "}";
            
            byte[] responseBytes = var5.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var6 = var1.getResponseBody();
            var6.write(responseBytes);
            var6.close();
         }
      }
   }

   static class UpdateBudgetHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            double var5 = Double.parseDouble(var3.get("newBudget"));
            boolean success = (new UserDAO()).updateMonthlyBudget(var4, var5);
            String var7 = success ? "{\"status\":\"success\"}" : "{\"status\":\"error\",\"message\":\"Budget save execution failure.\"}";
            
            byte[] responseBytes = var7.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var8 = var1.getResponseBody();
            var8.write(responseBytes);
            var8.close();
         }
      }
   }

   static class ChangePasswordHandler implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         main.configureCorsHeaders(var1);
         if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
            var1.sendResponseHeaders(204, -1L);
         } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
            String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> var3 = main.parseFormData(var2);
            int var4 = Integer.parseInt(var3.get("userId"));
            String var5 = var3.get("oldPass");
            String var6 = var3.get("newPass");
            boolean success = (new UserDAO()).changePassword(var4, var5, var6);
            String var7 = success ? "{\"status\":\"success\"}" : "{\"status\":\"error\",\"message\":\"Old password does not match or user tracking invalid!\"}";
            
            byte[] responseBytes = var7.getBytes(StandardCharsets.UTF_8);
            var1.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            var1.sendResponseHeaders(200, (long)responseBytes.length);
            OutputStream var8 = var1.getResponseBody();
            var8.write(responseBytes);
            var8.close();
         }
      }
   }
}