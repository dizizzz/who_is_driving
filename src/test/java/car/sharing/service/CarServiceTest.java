package car.sharing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import car.sharing.dto.car.CarDto;
import car.sharing.dto.car.CarRequestDto;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.mapper.CarMapper;
import car.sharing.model.car.Car;
import car.sharing.model.car.CarType;
import car.sharing.repository.CarRepository;
import car.sharing.service.impl.CarServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private CarDto carDto;
    private CarRequestDto requestDto;

    @BeforeEach
    public void setup() {
        car = new Car();
        car.setId(1L);
        car.setModel("Model 1");
        car.setBrand("Brand 1");
        car.setType(CarType.SUV);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(10.00));

        carDto = new CarDto()
                .setId(car.getId())
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());
    }

    @Test
    @DisplayName("Verify the correct car was added")
    public void addCar_WithValidCarRequestDto_ReturnCarDto() {
        requestDto = new CarRequestDto()
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());

        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);
        when(carMapper.toModel(requestDto)).thenReturn(car);

        CarDto actual = carService.add(requestDto);

        assertNotNull(actual);
        assertEquals(car.getId(), actual.getId());
    }

    @Test
    @DisplayName("Find all cars with pagination")
    void findAll_WithValidPageable_ReturnCarList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("brand"));

        Page<Car> carPage = new PageImpl<>(List.of(car), pageable, 1);

        when(carRepository.findAll(pageable)).thenReturn((carPage));
        List<CarDto> carList = carService.findAll(pageable);

        assertEquals(1, carList.size());
    }

    @Test
    @DisplayName("Verify the correct car was returned when car exists")
    public void getCarById_WithValidId_ReturnCarDto() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actual = carService.getById(car.getId());

        assertEquals(car.getId(), actual.getId());
    }

    @Test
    @DisplayName("Given incorrect id, check if returns exception")
    public void getById_WithInvalidId_ThrowsEntityNotFoundException() {
        Long carId = 100L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.getById(carId)
        );

        String expected = "Can`t find car by id " + carId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update car by id")
    void updateById_WithValidId_ReturnUpdatedCarDto() {
        requestDto = new CarRequestDto()
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actual = carService.updateById(car.getId(), requestDto);

        assertNotNull(actual);
        assertEquals(car.getId(), actual.getId());
        verify(carRepository, times(1)).save(car);
    }

    @Test
    @DisplayName("Given incorrect id, check if updateById throws EntityNotFoundException")
    void updateById_WithInvalidId_ThrowsEntityNotFoundException() {
        Long invalidCarId = 100L;
        requestDto = new CarRequestDto()
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());

        when(carRepository.findById(invalidCarId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.updateById(invalidCarId, requestDto)
        );

        String expected = "Can`t find car by id " + invalidCarId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Given correct id, check if car is deleted")
    void deleteById_WithValidCarId_VerifyDeletion() {
        carService.deleteById(car.getId());

        verify(carRepository, times(1)).deleteById(car.getId());
    }
}
