package car.sharing.mapper;

import car.sharing.config.MapperConfig;
import car.sharing.dto.rental.RentalDto;
import car.sharing.model.rental.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    RentalDto toDto(Rental rental);

    Rental toModel(RentalDto rentalDto);
}
