package api.orders;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String inputJson = "";
        try (BufferedReader reader = req.getReader()) {
            inputJson = reader.readLine();
        }

        if (inputJson == null || inputJson.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Empty or null JSON\" }");
            return;
        }

        // Parse the JSON string into a Map
        Map<String, String> parsedData = parseJsonToMap(inputJson.trim());

        String orderNumber = parsedData.get("orderNumber");

        if (orderNumber == null || orderNumber.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Invalid or missing orderNumber\" }");
            return;
        }

        long newOrderId = idGenerator.incrementAndGet();

        String jsonResponse = "{ \"id\": \"" + newOrderId + "\"";

        for (Map.Entry<String, String> entry : parsedData.entrySet()) {
            if (entry.getKey().equals("orderNumber")) {
                jsonResponse += ", \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
            }
        }
        jsonResponse += " }";

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(jsonResponse);
    }

    private Map<String, String> parseJsonToMap(String json) {

        json = json.trim().substring(1, json.length() - 1).trim();

        String[] keyValuePairs = json.split(",");

        Map<String, String> dataMap = new HashMap<>();

        // Process each key-value pair
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();
                dataMap.put(key, value);
            }
        }

        return dataMap;
    }
}
