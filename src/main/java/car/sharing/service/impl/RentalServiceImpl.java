package car.sharing.service.impl;

import car.sharing.dto.rental.RentalDto;
import car.sharing.dto.rental.RentalRequestDto;
import car.sharing.exception.CarNotAvailableException;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.mapper.RentalMapper;
import car.sharing.model.car.Car;
import car.sharing.model.rental.Rental;
import car.sharing.repository.CarRepository;
import car.sharing.repository.RentalRepository;
import car.sharing.repository.UserRepository;
import car.sharing.service.RentalService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public RentalDto add(RentalRequestDto requestDto, Long userId) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by id" + requestDto.getCarId())
        );
        if (car.getInventory() < 1) {
            throw new CarNotAvailableException("Car is not available");
        }
        car.setInventory(car.getInventory() - 1);//this is correct ?!
        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(requestDto.getReturnDate());
        rental.setCar(car);
        rental.setUser(userRepository.getReferenceById(userId));
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> findAllActiveRentals(Long userId, Pageable pageable) {
        return rentalRepository.getAllByUserIdAndActualReturnDateIsNull(userId, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public List<RentalDto> findAllNotActiveRentals(Long userId, Pageable pageable) {
        return rentalRepository
                .getAllByUserIdAndActualReturnDateIsNotNull(userId, pageable)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDto getById(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find rental by id" + id)
        );
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalDto setActualReturnDateById(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find rental by id" + id)
        );
        rental.setActualReturnDate(LocalDate.now());
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        return rentalMapper.toDto(rental);
    }
}
