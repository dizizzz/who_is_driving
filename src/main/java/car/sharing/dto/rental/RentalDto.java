package car.sharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate actualReturnDate;
    private LocalDate returnDate;
    private Long carId;
}
