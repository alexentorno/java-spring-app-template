package ee.api.orders;

import lombok.Getter;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRowHandler implements RowCallbackHandler {

    private final Map<Long, Order> orderMap = new HashMap<>();

    @Getter
    private Order singleOrder;

    @Getter
    private List<Order> result = new ArrayList<>();

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        long orderId = rs.getLong("order_id");
        String orderNumber = rs.getString("order_number");

        Order order = orderMap.getOrDefault(orderId, null);
        if (order == null) {
            order = new Order();
            order.setId(orderId);
            order.setOrderNumber(orderNumber);
            order.setOrderRows(new ArrayList<>());
            orderMap.put(orderId, order);
        }

        String itemName = rs.getString("item_name");
        if (itemName != null) {
            long orderRowId = rs.getLong("order_row_id");
            int quantity = rs.getInt("quantity");
            double price = rs.getDouble("price");

            OrderRow orderRow = new OrderRow(orderRowId, orderId, itemName, quantity, price);
            order.addOrderRow(orderRow);
        }

        result = new ArrayList<>(orderMap.values());
        if (orderMap.size() == 1) {
            singleOrder = result.getFirst();
        }
    }
}

