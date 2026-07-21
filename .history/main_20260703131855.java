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
            // Port 8080 par local gateway server initialize karein
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            
            // Endpoints map karein jo hamare frontend routes se connect honge
            server.createContext("/api/register", new RegisterHandler());
            server.createContext("/api/login", new LoginHandler());
            
            server.setExecutor(null); 
            System.out.println("====================================================");
            System.out.println("🚀 WalletWise Pure Java Connection Server is LIVE!");
            System.out.println("🔗 API Endpoint Gateway: http://localhost:8080");
            System.out.println("====================================================");
            server.start();
            
        } catch (IOException e) {
            System.out.println("❌ Failed to launch Java Server: " + e.getMessage());
        }
    }

    // --- UTILITY METHOD: FORM STREAM TO MAP TRANSLATOR ---
    private static Map<String, String> parseFormData(String formData) {
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

    // --- INTERCEPTOR HANDLING GLOBAL SECURITY RULES (CORS) ---
    private static void configureCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    // --- REGISTRATION REQUEST HANDLER ---
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

                String name = data.get("name");
                String email = data.get("email");
                String password = data.get("password");
                String role = data.get("role");

                String jsonResponse;
                UserDAO userDAO = new UserDAO();
                
                // Server Side Security Assertions
                if (!email.toLowerCase().endsWith("@gmail.com")) {
                    jsonResponse = "{\"status\":\"error\", \"message\":\"Email format must be @gmail.com!\"}";
                    exchange.sendResponseHeaders(400, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                } else {
                    try {
                        userDAO.registeruser(name, email, password, role);
                        jsonResponse = "{\"status\":\"success\", \"message\":\"Registration Complete!\"}";
                        exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                    } catch (Exception e) {
                        jsonResponse = "{\"status\":\"error\", \"message\":\"Database tracking rejected.\"}";
                        exchange.sendResponseHeaders(500, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                    }
                }

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }

    // --- AUTHENTICATION LOGIN HANDLER ---
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

                String email = data.get("email");
                String password = data.get("password");

                UserDAO userDAO = new UserDAO();
                int id = userDAO.loginuser(email, password);

                String jsonResponse;
                if (id != -1) {
                    // Admin Role Router validation mapping
                    String assignedRole = (email.toLowerCase().contains("admin") || email.equalsIgnoreCase("tushar@gmail.com")) ? "admin" : "user";
                    
                    jsonResponse = "{\"status\":\"success\", \"userId\":" + id + ", \"role\":\"" + assignedRole + "\", \"email\":\"" + email + "\"}";
                    exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                } else {
                    jsonResponse = "{\"status\":\"error\", \"message\":\"Invalid login details supplied!\"}";
                    exchange.sendResponseHeaders(401, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                }

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }
}