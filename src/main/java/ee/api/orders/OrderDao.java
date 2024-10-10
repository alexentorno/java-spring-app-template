package ee.api.orders;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {

    private final DataSource dataSource;

    public OrderDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Order findOrderWithId(Long id) {
        String query = "SELECT o.id AS order_id, o.order_number, r.item_name, r.quantity, r.price " +
                "FROM orders o LEFT JOIN order_rows r ON o.id = r.order_id WHERE o.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return mapOrderFromResultSet(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertOrder(Order order) throws SQLException {
        String orderQuery = "INSERT INTO orders (order_number) VALUES (?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, order.getOrderNumber());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                }
            }

            if (order.getOrderRows() != null) {
                for (OrderRow row : order.getOrderRows()) {
                    insertOrderRow(conn, order.getId(), row);
                }
            }
        }
    }

    private void insertOrderRow(Connection conn, Long orderId, OrderRow row) throws SQLException {
        String lineQuery = "INSERT INTO order_rows (order_id, item_name, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(lineQuery)) {
            ps.setLong(1, orderId);
            ps.setString(2, row.getItemName());
            ps.setInt(3, row.getQuantity());
            ps.setDouble(4, row.getPrice());
            ps.executeUpdate();
        }
    }

    public void deleteOrderWithId(Long orderId) throws SQLException {
        tryDeleteWithQueryAndOrderId("DELETE FROM order_rows WHERE order_id = ?", orderId);
        tryDeleteWithQueryAndOrderId("DELETE FROM orders WHERE id = ?", orderId);
    }

    private void tryDeleteWithQueryAndOrderId(String deleteQuery, Long orderId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteQuery)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        }
    }

    public List<Order> findAllOrders() {
        String query = "SELECT o.id AS order_id, o.order_number, r.item_name, r.quantity, r.price " +
                "FROM orders o LEFT JOIN order_rows r ON o.id = r.order_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            Map<Long, Order> orderMap = new HashMap<>();
            while (rs.next()) {
                long orderId = rs.getLong("order_id");
                String orderNumber = rs.getString("order_number");

                Order order = orderMap.computeIfAbsent(orderId, id -> new Order(id, orderNumber));
                if (rs.getString("item_name") != null) {
                    OrderRow orderRow = new OrderRow(rs.getString("item_name"), rs.getInt("quantity"), rs.getDouble("price"));
                    order.addOrderRow(orderRow);
                }
            }
            return new ArrayList<>(orderMap.values());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Order mapOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = null;
        while (rs.next()) {
            if (order == null) {
                order = new Order(rs.getLong("order_id"), rs.getString("order_number"));
            }
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            double price = rs.getDouble("price");
            if (itemName != null) {
                order.addOrderRow(new OrderRow(itemName, quantity, price));
            }
        }
        return order;
    }
}

