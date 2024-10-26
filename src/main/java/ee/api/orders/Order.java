package ee.api.orders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @NotNull
    private long id;

    @NotNull
    @Size(min = 2)
    private String orderNumber;

    @Valid
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
