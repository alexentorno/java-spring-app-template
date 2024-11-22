package ee.api.orders;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Table(name = "authorities")
@AllArgsConstructor
@NoArgsConstructor
public class Authorities {

    @NotNull
    private String authority;

}
