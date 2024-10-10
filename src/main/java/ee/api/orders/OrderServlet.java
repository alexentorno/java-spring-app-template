package ee.api.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ConnectionPoolFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {
    private OrderDao orderDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        DataSource dataSource = new ConnectionPoolFactory().createConnectionPool();
        orderDao = new OrderDao(dataSource);
        objectMapper = new ObjectMapper(); // Initialize once
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Order newOrder = deserializeJsonIntoOrder(req, resp);

        if (newOrder == null) { return; }

        if (isInvalidOrderNumber(newOrder.getOrderNumber())) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid or missing orderNumber");
            return;
        }

        try {
            orderDao.insertOrder(newOrder);
            sendJsonResponse(resp, HttpServletResponse.SC_CREATED, newOrder);
        } catch (SQLException e) {
            handleDatabaseError(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdParam = req.getParameter("id");

        if (orderIdParam != null && !orderIdParam.isEmpty()) {
            try {
                long orderId = Long.parseLong(orderIdParam);
                Order order = orderDao.findOrderWithId(orderId);
                if (order == null) {
                    sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
                } else {
                    sendJsonResponse(resp, HttpServletResponse.SC_OK, order);
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
            }
        } else {
            List<Order> orders = orderDao.findAllOrders();
            sendJsonResponse(resp, HttpServletResponse.SC_OK, orders);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdParam = req.getParameter("id");

        if (orderIdParam == null || orderIdParam.isEmpty()) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing order ID");
            return;
        }

        tryDeleteOrder(resp, orderIdParam);
    }

    private void tryDeleteOrder(HttpServletResponse resp, String idParam) throws IOException {
        try {
            long orderId = Long.parseLong(idParam);
            orderDao.deleteOrderWithId(orderId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            handleDatabaseError(resp, e);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
        }
    }

    private Order deserializeJsonIntoOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            return objectMapper.readValue(reader, Order.class);
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
            return null;
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        resp.getWriter().write(objectMapper.writeValueAsString(data));
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        resp.getWriter().write("{ \"error\": \"" + message + "\" }");
    }

    private void handleDatabaseError(HttpServletResponse resp, SQLException e) throws IOException {
        sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        e.printStackTrace();  // Consider logging this to a file or monitoring system
    }

    private boolean isInvalidOrderNumber(String orderNumber) {
        return orderNumber == null || orderNumber.isEmpty();
    }
}
