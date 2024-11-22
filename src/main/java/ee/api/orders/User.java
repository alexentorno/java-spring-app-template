package ee.api.orders;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    @Min(2)
    private String password;

    @NotNull
    private boolean enabled;

    @NotNull
    @Column(name = "first_name")
    @Min(2)
    private String firstName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities",
            joinColumns = @JoinColumn(name = "username",
                    referencedColumnName = "username"))
    private List<Authorities> authorities;
}
