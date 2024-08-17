package car.sharing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import car.sharing.dto.rental.RentalDto;
import car.sharing.dto.rental.RentalRequestDto;
import car.sharing.exception.CarNotAvailableException;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.mapper.RentalMapper;
import car.sharing.model.car.Car;
import car.sharing.model.car.CarType;
import car.sharing.model.rental.Rental;
import car.sharing.model.user.Role;
import car.sharing.model.user.RoleName;
import car.sharing.model.user.User;
import car.sharing.repository.CarRepository;
import car.sharing.repository.RentalRepository;
import car.sharing.repository.UserRepository;
import car.sharing.service.impl.RentalServiceImpl;
import car.sharing.service.telegram.TelegramNotificationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TelegramNotificationService notificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Rental rental;
    private RentalDto rentalDto;
    private RentalRequestDto rentalRequestDto;
    private Car car;
    private User user;
    private Role role;

    @BeforeEach
    public void setup() {
        car = new Car();
        car.setId(1L);
        car.setModel("Model 1");
        car.setBrand("Brand 1");
        car.setType(CarType.SUV);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(10.00));

        role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);

        user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setPassword("123456789encodedPassword");
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setRoles(Set.of(role));

        rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2024, 8, 10));
        rental.setReturnDate(LocalDate.of(2024, 8, 12));
        rental.setActualReturnDate(LocalDate.of(2024, 8, 15));
        rental.setCar(car);
        rental.setUser(user);

        rentalDto = new RentalDto();
        rentalDto.setId(rental.getId());
        rentalDto.setRentalDate(rental.getRentalDate());
        rentalDto.setReturnDate(rental.getReturnDate());
        rentalDto.setCarId(car.getId());
    }

    @Test
    @DisplayName("Add rental with valid request")
    public void addRental_ValidRequest_ReturnsRentalDto() {
        rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.setCarId(car.getId());
        rentalRequestDto.setReturnDate(rental.getReturnDate());

        when(carRepository.findById(rentalRequestDto.getCarId())).thenReturn(Optional.of(car));
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        RentalDto result = rentalService.add(rentalRequestDto, 1L);

        assertNotNull(result);
        assertEquals(rental.getId(), result.getId());
        verify(carRepository, times(1)).findById(rentalRequestDto.getCarId());
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(notificationService, times(1)).sendMessage(anyString());
    }

    @Test
    @DisplayName("Add rental with unavailable car")
    public void addRental_CarNotAvailable_ThrowsException() {
        car.setInventory(0);

        rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.setCarId(car.getId());
        rentalRequestDto.setReturnDate(rental.getReturnDate());

        when(carRepository.findById(rentalRequestDto.getCarId())).thenReturn(Optional.of(car));

        assertThrows(CarNotAvailableException.class,
                () -> rentalService.add(rentalRequestDto, 1L));
    }

    @Test
    @DisplayName("Find all active rentals for a user")
    public void findAllActiveRentals_ValidUserId_ReturnsRentalList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(rentalRepository.getAllByUserIdAndActualReturnDateIsNull(anyLong(), eq(pageable)))
                .thenReturn(List.of(rental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        List<RentalDto> rentals = rentalService.findAllActiveRentals(1L, pageable);

        assertNotNull(rentals);
        assertEquals(1, rentals.size());
        assertEquals(rentalDto.getId(), rentals.get(0).getId());
    }

    @Test
    @DisplayName("Find all inactive rentals for a user")
    public void findAllNotActiveRentals_ValidUserId_ReturnsRentalList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(rentalRepository.getAllByUserIdAndActualReturnDateIsNotNull(anyLong(), eq(pageable)))
                .thenReturn(List.of(rental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        List<RentalDto> rentals = rentalService.findAllNotActiveRentals(1L, pageable);

        assertNotNull(rentals);
        assertEquals(1, rentals.size());
        assertEquals(rentalDto.getId(), rentals.get(0).getId());
    }

    @Test
    @DisplayName("Get rental by ID")
    public void getById_ValidId_ReturnsRentalDto() {
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        RentalDto result = rentalService.getById(rental.getId());

        assertNotNull(result);
        assertEquals(rental.getId(), result.getId());
    }

    @Test
    @DisplayName("Get rental by ID with non-existing ID")
    public void getById_InvalidId_ThrowsException() {
        Long invalidId = 99L;
        when(rentalRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rentalService.getById(invalidId));
    }

    @Test
    @DisplayName("Set actual return date by ID")
    public void setActualReturnDateById_ValidId_ReturnsUpdatedRentalDto() {
        LocalDate fixedDate = LocalDate.of(2024, 8, 15);
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental savedRental = invocation.getArgument(0);
            savedRental.setActualReturnDate(fixedDate);
            return savedRental;
        });
        when(rentalMapper.toDto(any(Rental.class))).thenAnswer(invocation -> {
            Rental argRental = invocation.getArgument(0);
            RentalDto dto = new RentalDto();
            dto.setId(argRental.getId());
            dto.setActualReturnDate(argRental.getActualReturnDate());
            return dto;
        });

        RentalDto result = rentalService.setActualReturnDateById(rental.getId());

        assertNotNull(result);
        assertEquals(rental.getId(), result.getId());
        assertEquals(fixedDate, result.getActualReturnDate());
        verify(carRepository, times(1)).findById(car.getId());
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    @DisplayName("Check overdue rentals")
    public void checkOverdueRentals_WithOverdueRentals_SendsNotification() {
        List<Rental> overdueRentals = List.of(rental);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        when(rentalRepository
                .getAllByReturnDateBeforeAndActualReturnDateIsNull(tomorrow))
                .thenReturn(overdueRentals);

        rentalService.checkOverdueRentals();

        verify(notificationService, times(1)).sendMessage(anyString());
    }
}
