package ee.api.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRow {


    private Long id;

    private Long orderId;

    @NotNull
    private String itemName;

    @NotNull
    @Min(1)
    private int quantity;

    @NotNull
    @Min(1)
    private double price;

    public OrderRow(String itemName, int quantity, double price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }
}