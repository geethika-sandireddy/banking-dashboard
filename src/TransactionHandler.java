import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.sql.*;

public class TransactionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        String query = exchange.getRequestURI().getQuery();
        String userId = "1";
        if (query != null && query.contains("userId=")) {
            userId = query.split("userId=")[1].split("&")[0];
        }

        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM transactions WHERE user_id=? ORDER BY created_at DESC"
            );
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = stmt.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"type\":\"").append(rs.getString("type")).append("\",")
                    .append("\"amount\":").append(rs.getDouble("amount")).append(",")
                    .append("\"description\":\"").append(rs.getString("description")).append("\",")
                    .append("\"status\":\"").append(rs.getString("status")).append("\",")
                    .append("\"date\":\"").append(rs.getTimestamp("created_at")).append("\"")
                    .append("}");
                first = false;
            }
        } catch (SQLException e) {
            json = new StringBuilder("{\"error\":\"" + e.getMessage() + "\"}");
        }
        json.append("]");
        String response = json.toString();
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}