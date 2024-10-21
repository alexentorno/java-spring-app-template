package ee.api.orders;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long id;
    private String orderNumber;
    private List<OrderRow> orderRows;

    public Order (long id, String orderNumber) {
        this.id = id;
        this.orderNumber = orderNumber;
    }

    public void addOrderRow(OrderRow row) {
        if (orderRows == null) {
            orderRows = new java.util.ArrayList<>();
        }
        orderRows.add(row);
    }
}
