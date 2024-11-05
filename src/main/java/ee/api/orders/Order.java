package ee.api.orders;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @SequenceGenerator(name = "my_seq", sequenceName = "seq1", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq")
    private Long id;

    @NotNull
    @Column(name = "order_number")
    @Size(min = 2)
    private String orderNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_rows",
            joinColumns = @JoinColumn(name = "orders_id",
                    referencedColumnName = "id"))
    private List<OrderRow> orderRows;

}
