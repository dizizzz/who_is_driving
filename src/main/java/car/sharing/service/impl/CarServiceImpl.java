package car.sharing.service.impl;

import car.sharing.dto.car.CarDto;
import car.sharing.dto.car.CarRequestDto;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.mapper.CarMapper;
import car.sharing.model.car.Car;
import car.sharing.repository.CarRepository;
import car.sharing.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto add(CarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by id " + id));
        return carMapper.toDto(car);
    }

    @Override
    public CarDto updateById(Long id, CarRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by id " + id));
        Car updatedCar = carMapper.toModel(requestDto);
        updatedCar.setId(id);
        return carMapper.toDto(carRepository.save(updatedCar));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}
