package car.sharing.dto.car;

import car.sharing.model.car.CarType;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private CarType type;
    private Integer inventory;
    private BigDecimal dailyFee;
}
