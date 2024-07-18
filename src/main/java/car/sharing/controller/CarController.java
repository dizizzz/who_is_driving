package car.sharing.controller;

import car.sharing.dto.car.CarDto;
import car.sharing.dto.car.CarRequestDto;
import car.sharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for cars managing")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new car", description = "Add a new car")
    public CarDto addCar(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.add(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all cars", description = "Get a list of all available cars")
    public List<CarDto> getAll(Authentication authentication, Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get the car by ID", description = "Get car's detailed information")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update the car by ID",
            description = "Update the car by ID (also manage inventory)")
    public CarDto updateCarById(@PathVariable Long id,
                                  @RequestBody CarRequestDto requestDto) {
        return carService.updateById(id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete the car by ID", description = "Delete the car by ID")
    public void deleteCarById(@PathVariable Long id) {
        carService.deleteById(id);
    }

}
