package ee.api.orders;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class OrderDao {

    private JdbcClient jdbcClient;

    public OrderDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Order> findAllOrders() {
        String query = "SELECT o.id AS order_id, o.order_number, r.id AS order_row_id, r.item_name, r.quantity, r.price " +
                "FROM orders o LEFT JOIN order_rows r ON o.id = r.order_id";

        var handler = new OrderRowHandler();
        jdbcClient.sql(query).query(handler);
        return handler.getResult();
    }



    public Order findOrderWithId(Long id) {
        String query = "SELECT o.id AS order_id, o.order_number, r.id AS order_row_id, r.item_name, r.quantity, r.price " +
                "FROM orders o LEFT JOIN order_rows r ON o.id = r.order_id WHERE o.id = ?";

        var handler = new OrderRowHandler();

        jdbcClient.sql(query)
                .param(id)
                .query(handler);

        List<Order> orders = handler.getResult();
        return orders.isEmpty() ? null : handler.getSingleOrder();
    }


    public Order insertOrder(Order order) throws SQLException {
        String orderQuery = "INSERT INTO orders (order_number) VALUES (?);";

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcClient.sql(orderQuery)
                .param(1, order.getOrderNumber())
                .update(kh, "id");

        Long orderId = kh.getKey().longValue();

        order.setId(orderId);

        if (order.getOrderRows() != null) {
            for (OrderRow row : order.getOrderRows()) {
                insertOrderRow(kh.getKey().longValue(), row);
            }
        }
        return order;
    }

    private void insertOrderRow(Long orderId, OrderRow row) {
        String query = "INSERT INTO order_rows (order_id, item_name, quantity, price) VALUES (?, ?, ?, ?);";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcClient.sql(query)
                .param(1, orderId)
                .param(2, row.getItemName())
                .param(3, row.getQuantity())
                .param(4, row.getPrice())
                .update(kh, "id");

        Long rowId = kh.getKey().longValue();

        row.setId(rowId);
        row.setOrderId(orderId);
    }

    public void deleteOrderWithId(Long orderId) throws SQLException {
        tryDeleteWithQueryAndOrderId("DELETE FROM order_rows WHERE order_id = ?", orderId);
        tryDeleteWithQueryAndOrderId("DELETE FROM orders WHERE id = ?", orderId);
    }

    private void tryDeleteWithQueryAndOrderId(String deleteQuery, Long orderId) {
        jdbcClient.sql(deleteQuery)
                .param(1, orderId)
                .update();
    }
}

