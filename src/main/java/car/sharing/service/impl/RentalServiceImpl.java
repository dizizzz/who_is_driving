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
import car.sharing.service.telegram.TelegramNotificationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final TelegramNotificationService notificationService;

    @Override
    public RentalDto add(RentalRequestDto requestDto, Long userId) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by id" + requestDto.getCarId())
        );
        if (car.getInventory() < 1) {
            throw new CarNotAvailableException("Car is not available");
        }
        car.setInventory(car.getInventory() - 1);
        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(requestDto.getReturnDate());
        rental.setCar(car);
        rental.setUser(userRepository.getReferenceById(userId));
        Rental savedRental = rentalRepository.save(rental);

        String message = "<b>New Rental Created!</b>\n"
                + "Rental ID: " + rental.getId() + "\n"
                + "User ID: " + userId + "\n"
                + "Car ID: " + car.getId() + " \n"
                + "Return Date: " + rental.getReturnDate();
        notificationService.sendMessage(message);
        return rentalMapper.toDto(savedRental);
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

    @Scheduled(cron = "0 0 8 * * *")// at 8 am
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<Rental> overdueRentals =
                rentalRepository.getAllByReturnDateBeforeAndActualReturnDateIsNull(tomorrow);

        if (overdueRentals.isEmpty()) {
            notificationService.sendMessage("No rentals overdue today!");
        } else {
            overdueRentals.forEach(
                    rental -> {
                        String message = "<b>Overdue Rental Notification!</b>\n"
                                + "Rental ID: " + rental.getId() + "\n"
                                + "User ID: " + rental.getUser().getId() + "\n"
                                + "Car ID: " + rental.getCar().getId() + " \n"
                                + "Return Date: " + rental.getReturnDate();
                        notificationService.sendMessage(message);
                    }
            );
        }

    }
}
