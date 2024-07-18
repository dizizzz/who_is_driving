package car.sharing.controller;

import car.sharing.dto.rental.RentalDto;
import car.sharing.dto.rental.RentalRequestDto;
import car.sharing.model.user.User;
import car.sharing.service.RentalService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for rentals managing")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new rental", description = "Add a rental car")
    public RentalDto addRental(@RequestBody @Valid RentalRequestDto requestDto,
                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.add(requestDto, user.getId());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    @Operation(summary = "Get all rentals", description = "Get a list of all rental "
            + " by user ID and whether the rental is still active or not")
    public List<RentalDto> getAllActiveRentals(
            @RequestParam(name = "is_active") boolean isActive,
                                               Authentication authentication,
                                               Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        if (isActive) {
            return rentalService.findAllActiveRentals(user.getId(), pageable);
        } else {
            return rentalService.findAllNotActiveRentals(user.getId(), pageable);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific rental by ID", description = "Get specific rental by ID")
    public RentalDto getRentalById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}/return")
    @Operation(summary = "Return the rental by ID", description = "Set actual return date by ID")
    public RentalDto returnRental(@PathVariable Long id) {
        return rentalService.setActualReturnDateById(id);
    }
}
