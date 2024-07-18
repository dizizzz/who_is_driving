package car.sharing.service;

import car.sharing.dto.rental.RentalDto;
import car.sharing.dto.rental.RentalRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto add(RentalRequestDto requestDto, Long userId);

    List<RentalDto> findAllActiveRentals(Long userId, Pageable pageable);

    List<RentalDto> findAllNotActiveRentals(Long userId, Pageable pageable);

    RentalDto getById(Long id);

    RentalDto setActualReturnDateById(Long id);
}
