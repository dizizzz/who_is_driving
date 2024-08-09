package car.sharing.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentRequestDto {
    @NotNull
    private String type;
    @Min(1)
    private Long rentalId;
}
