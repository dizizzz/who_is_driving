package car.sharing.dto.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalRequestDto {
    @NotBlank
    private LocalDate returnDate;
    @Min(1)
    private Long carId;
}
