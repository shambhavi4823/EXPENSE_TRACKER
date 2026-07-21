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

public class main {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            
            // Web Integration Endpoints
            server.createContext("/api/register", new RegisterHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/user-dashboard", new UserDashboardHandler());
            
            server.setExecutor(null);
            System.out.println("====================================================");
            System.out.println("🚀 WalletWise Active Web Connection Engine Running!");
            System.out.println("🔗 Backend Target Gateway Address: http://localhost:8080");
            System.out.println("====================================================");
            server.start();
        } catch (IOException e) {
            System.out.println("❌ Server Initiation Fault: " + e.getMessage());
        }
    }

    public static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) return map;
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
            map.put(key, value);
        }
        return map;
    }

    private static void configureCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            configureCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = parseFormData(requestBody);
                String jsonResponse;
                try {
                    new UserDAO().registeruser(data.get("name"), data.get("email"), data.get("password"), data.get("role"));
                    jsonResponse = "{\"status\":\"success\"}";
                    exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                } catch (Exception e) {
                    jsonResponse = "{\"status\":\"error\",\"message\":\"Registration Error Exception.\"}";
                    exchange.sendResponseHeaders(400, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                }
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            configureCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = parseFormData(requestBody);
                int id = new UserDAO().loginuser(data.get("email"), data.get("password"));
                String jsonResponse;
                if (id != -1) {
                    String role = (data.get("email").toLowerCase().contains("admin") || data.get("email").equalsIgnoreCase("tushar@gmail.com")) ? "admin" : "user";
                    jsonResponse = "{\"status\":\"success\",\"userId\":" + id + ",\"role\":\"" + role + "\",\"email\":\"" + data.get("email") + "\"}";
                    exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                } else {
                    jsonResponse = "{\"status\":\"error\",\"message\":\"Invalid details provided!\"}";
                    exchange.sendResponseHeaders(401, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                }
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }

    static class UserDashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            configureCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = parseFormData(requestBody);
                int userId = Integer.parseInt(data.get("userId"));
                String jsonResponse = new ExpenseDAO().getDashboardData(userId);
                exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }
}