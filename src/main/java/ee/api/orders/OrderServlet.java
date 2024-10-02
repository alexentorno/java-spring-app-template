package ee.api.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ConnectionPoolFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        DataSource pool = new ConnectionPoolFactory().createConnectionPool();

        OrderDao orderDao = new OrderDao(pool);

        ObjectMapper objectMapper = new ObjectMapper();
        Order newOrder;

        newOrder = deserializeJsonIntoOrder(req, resp, objectMapper);

        if (newOrder == null) return;

        if (newOrder.getOrderNumber() == null || newOrder.getOrderNumber().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Invalid or missing orderNumber\" }");
            return;
        }

        tryInsertOrder(resp, orderDao, newOrder, objectMapper);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataSource pool = new ConnectionPoolFactory().createConnectionPool();
        OrderDao orderDao = new OrderDao(pool);

        // Retrieve the `id` parameter if it exists
        String idParam = req.getParameter("id");

        ObjectMapper objectMapper = new ObjectMapper();

        // If `id` parameter is present, return the order with that ID
        if (idParam != null && !idParam.isEmpty()) {
            try {
                long orderId = Long.parseLong(idParam);
                Order order = orderDao.findOrderWithId(orderId);

                if (order == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{ \"error\": \"Order not found\" }");
                } else {
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(order));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{ \"error\": \"Invalid order ID format\" }");
            }
        } else {
            // If `id` parameter is not provided, return all orders
            List<Order> orders = orderDao.findOrders();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(orders));
        }
    }


    private static void tryInsertOrder(HttpServletResponse resp, OrderDao orderDao, Order newOrder, ObjectMapper objectMapper) throws IOException {
        try {
            orderDao.insertOrder(newOrder);

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(newOrder));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{ \"error\": \"Database error\" }");
            throw new RuntimeException(e);
        }
    }

    private static Order deserializeJsonIntoOrder(HttpServletRequest req, HttpServletResponse resp, ObjectMapper objectMapper) throws IOException {
        Order newOrder;
        try (BufferedReader reader = req.getReader()) {
            newOrder = objectMapper.readValue(reader, Order.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Invalid JSON format\" }");
            return null;
        }
        return newOrder;
    }
}
