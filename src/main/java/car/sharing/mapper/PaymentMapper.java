package car.sharing.mapper;

import car.sharing.config.MapperConfig;
import car.sharing.dto.payment.PaymentDto;
import car.sharing.model.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDto toDto(Payment payment);

    Payment toModel(PaymentDto paymentDto);
}
