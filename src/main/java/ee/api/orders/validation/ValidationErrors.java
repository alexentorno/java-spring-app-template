package ee.api.orders.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrors {
    private List<ValidationError> errors;

}