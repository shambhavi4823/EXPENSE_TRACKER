import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class main {
    public main() {
    }

    public static void main(String[] var0) throws IOException {
        HttpServer var1 = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Expense History Endpoints
        var1.createContext("/api/get-expenses", new GetExpensesHandler());
        var1.createContext("/api/delete-expense", new DeleteExpenseHandler());
        var1.createContext("/api/edit-expense", new EditExpenseHandler());
        
        // Core User Management & Dashboard Endpoints
        var1.createContext("/api/add-expense", new AddExpenseHandler());
        var1.createContext("/api/dashboard", new DashboardHandler());
        var1.createContext("/api/update-budget", new UpdateBudgetHandler());

        var1.setExecutor((java.util.concurrent.Executor)null);
        System.out.println("🚀 WalletWise Backend Server started successfully on http://localhost:8080");
        var1.start();
    }

    public static void configureCorsHeaders(HttpExchange var0) {
        var0.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        var0.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        var0.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    public static Map<String, String> parseFormData(String var0) {
        HashMap var1 = new HashMap();
        String[] var2 = var0.split("&");
        String[] var3 = var2;
        int var4 = var2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            String[] var7 = var6.split("=");
            if (var7.length > 1) {
                try {
                    var1.put(URLDecoder.decode(var7[0], StandardCharsets.UTF_8.name()), URLDecoder.decode(var7[1], StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException var9) {
                    var9.printStackTrace();
                }
            }
        }

        return var1;
    }

    // --- EXPENSE LOG ENGINE HANDLERS ---

    static class GetExpensesHandler implements HttpHandler {
        public void handle(HttpExchange var1) throws IOException {
            main.configureCorsHeaders(var1);
            if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
                var1.sendResponseHeaders(204, -1L);
            } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
                String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map var3 = main.parseFormData(var2);
                int var4 = Integer.parseInt((String)var3.get("userId"));
                
                String var5 = (new ExpenseDAO()).getUserExpensesJson(var4);
                var1.sendResponseHeaders(200, (long)var5.getBytes(StandardCharsets.UTF_8).length);
                OutputStream var6 = var1.getResponseBody();
                var6.write(var5.getBytes(StandardCharsets.UTF_8));
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
                Map var3 = main.parseFormData(var2);
                int var4 = Integer.parseInt((String)var3.get("userId"));
                int var5 = Integer.parseInt((String)var3.get("expenseId"));
                
                boolean success = (new ExpenseDAO()).deleteExpense(var4, var5);
                String var6 = success ? "{\"status\":\"success\"}" : "{\"status\":\"error\"}";
                var1.sendResponseHeaders(200, (long)var6.getBytes(StandardCharsets.UTF_8).length);
                OutputStream var7 = var1.getResponseBody();
                var7.write(var6.getBytes(StandardCharsets.UTF_8));
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
                Map var3 = main.parseFormData(var2);
                
                int userId = Integer.parseInt((String)var3.get("userId"));
                int expenseId = Integer.parseInt((String)var3.get("expenseId"));
                String type = (String)var3.get("item_type");
                String name = (String)var3.get("item_name");
                int qty = Integer.parseInt((String)var3.get("quantity"));
                double price = Double.parseDouble((String)var3.get("price"));
                
                boolean success = (new ExpenseDAO()).editExpense(userId, expenseId, type, name, qty, price);
                String response = success ? "{\"status\":\"success\"}" : "{\"status\":\"error\",\"message\":\"Failed to save modifications.\"}";
                
                var1.sendResponseHeaders(200, (long)response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = var1.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }

    // --- CORE TRANSACTION HANDLERS ---

    static class AddExpenseHandler implements HttpHandler {
        public void handle(HttpExchange var1) throws IOException {
            main.configureCorsHeaders(var1);
            if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
                var1.sendResponseHeaders(204, -1L);
            } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
                String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map var3 = main.parseFormData(var2);
                int userId = Integer.parseInt((String)var3.get("userId"));
                String type = (String)var3.get("item_type");
                String name = (String)var3.get("item_name");
                int qty = Integer.parseInt((String)var3.get("quantity"));
                double price = Double.parseDouble((String)var3.get("price"));

                String response = (new ExpenseDAO()).addExpense(userId, type, name, qty, price);
                var1.sendResponseHeaders(200, (long)response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = var1.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }

    static class DashboardHandler implements HttpHandler {
        public void handle(HttpExchange var1) throws IOException {
            main.configureCorsHeaders(var1);
            if ("OPTIONS".equalsIgnoreCase(var1.getRequestMethod())) {
                var1.sendResponseHeaders(204, -1L);
            } else if ("POST".equalsIgnoreCase(var1.getRequestMethod())) {
                String var2 = new String(var1.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map var3 = main.parseFormData(var2);
                int userId = Integer.parseInt((String)var3.get("userId"));

                String response = (new ExpenseDAO()).getDashboardData(userId);
                var1.sendResponseHeaders(200, (long)response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = var1.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
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
                Map var3 = main.parseFormData(var2);
                int userId = Integer.parseInt((String)var3.get("userId"));
                double limit = Double.parseDouble((String)var3.get("budget_limit"));

                (new ExpenseDAO()).updateMonthlyBudget(userId, limit);
                String response = "{\"status\":\"success\"}";
                var1.sendResponseHeaders(200, (long)response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = var1.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }
}