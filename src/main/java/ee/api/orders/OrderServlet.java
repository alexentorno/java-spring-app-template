package ee.api.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
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

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        if (context.getAttribute("orders") == null) {
            context.setAttribute("orders", new HashMap<Long, Order>());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //get context
        ServletContext context = getServletContext();
        //find orders
        Map<Long, Order> orders = (Map<Long, Order>) context.getAttribute("orders");

        ObjectMapper objectMapper = new ObjectMapper();
        Order newOrder;

        try (BufferedReader reader = req.getReader()) {
            newOrder = objectMapper.readValue(reader, Order.class);  //deserialize json into Order object
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Invalid JSON format\" }");
            return;
        }

        if (newOrder.getOrderNumber() == null || newOrder.getOrderNumber().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Invalid or missing orderNumber\" }");
            return;
        }

        long newOrderId = ID_GENERATOR.incrementAndGet();
        newOrder.setId(newOrderId);  // Set the generated ID for the new order

        orders.put(newOrderId, newOrder);  // Store the new order in the map

        // Return the newly created order in JSON format
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(newOrder));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        Map<Long, Order> orders = (Map<Long, Order>) context.getAttribute("orders");

        String idParam = req.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Missing 'id' parameter\" }");
            return;
        }

        long orderId;
        try {
            orderId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Invalid 'id' parameter format\" }");
            return;
        }

        Order order = orders.get(orderId);

        if (order == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{ \"error\": \"Order not found\" }");
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(order));  // Serialize the order to JSON
        }
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
