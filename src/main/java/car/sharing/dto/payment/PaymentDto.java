package car.sharing.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentDto {
    private Long id;
    private String status;
    private String type;
    private Long rentalId;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
