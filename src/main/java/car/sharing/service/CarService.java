package car.sharing.service;

import car.sharing.dto.car.CarDto;
import car.sharing.dto.car.CarRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto add(CarRequestDto requestDto);

    List<CarDto> findAll(Pageable pageable);

    CarDto getById(Long id);

    CarDto updateById(Long id, CarRequestDto requestDto);

    void deleteById(Long id);
}
