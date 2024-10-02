package ee.api.orders;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    private DataSource dataSource;

    public OrderDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Order> findOrders() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "select id, order_number from orders");

            List<Order> orders = new ArrayList<>();

            while (rs.next()){
                Order order = new Order(rs.getLong("id"),
                        rs.getString("order_number")
                );
                orders.add(order);
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Order findOrderWithId(Long id) {
        String query = "select id, order_number from orders where id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return new Order(rs.getLong("id"),
                        rs.getString("order_number")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Order insertOrder(Order order) throws SQLException {
        String query = "INSERT INTO orders (order_number) VALUES (?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Set the order number
            ps.setString(1, order.getOrderNumber());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            //Retrieve the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    //Assign the generated ID to the order object
                    order.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            return order;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
