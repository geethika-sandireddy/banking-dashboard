import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("AUTH REQUEST RECEIVED");
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            String body = br.readLine();
            System.out.println("BODY: " + body);
            Map<String, String> params = parseParams(body);
            String username = params.get("username");
            String password = params.get("password");
            System.out.println("Username: " + username + " Password: " + password);
            String response;
            try (Connection conn = DatabaseConnection.getConnection()) {
                System.out.println("DB CONNECTED");
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, full_name FROM users WHERE username=? AND password=?"
                );
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    response = "{\"success\":true,\"userId\":" + rs.getInt("id") + ",\"name\":\"" + rs.getString("full_name") + "\"}";
                } else {
                    response = "{\"success\":false,\"message\":\"Invalid credentials\"}";
                }
            } catch (SQLException e) {
                System.out.println("DB ERROR: " + e.getMessage());
                response = "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}";
            }
            sendResponse(exchange, response);
        }
    }

    private Map<String, String> parseParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) return params;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                try {
                    params.put(URLDecoder.decode(pair[0], "UTF-8"), URLDecoder.decode(pair[1], "UTF-8"));
                } catch (Exception e) { }
            }
        }
        return params;
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}