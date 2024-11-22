package ee.api.orders;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Data
@Table(name = "order_rows")
@AllArgsConstructor
@NoArgsConstructor
public class OrderRow {

//    private Long orderId;

    @Column(name = "item_name")
    @NotNull
    private String itemName;

    @NotNull
    @Min(1)
    private int price;

    @NotNull
    @Min(1)
    private int quantity;
}