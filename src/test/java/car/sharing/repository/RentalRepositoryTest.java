package car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import car.sharing.model.rental.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/users/add-users.sql",
        "classpath:database/users/add-users-roles.sql",
        "classpath:database/cars/add-cars.sql",
        "classpath:database/rentals/add-rentals.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/rentals/remove-rentals.sql",
        "classpath:database/cars/remove-cars.sql",
        "classpath:database/users/remove-users-roles.sql",
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Find all rentals by user with null actual return date")
    public void getAllByUserIdAndActualReturnDateIsNull_correctUserId_ok() {
        Long userId = 2L;
        List<Rental> rentals = rentalRepository
                .getAllByUserIdAndActualReturnDateIsNull(userId, Pageable.unpaged());
        assertFalse(rentals.isEmpty(), "Rentals should not be empty");
        assertEquals(1, rentals.size());
    }

    @Test
    @DisplayName("Find all rentals by user with non-null actual return date")
    public void getAllByUserIdAndActualReturnDateIsNotNull_correctUserId_ok() {
        Long userId = 3L;
        List<Rental> rentals = rentalRepository
                .getAllByUserIdAndActualReturnDateIsNotNull(userId, Pageable.unpaged());
        assertFalse(rentals.isEmpty(), "Rentals should not be empty");
        assertEquals(1, rentals.size());
    }

    @Test
    @DisplayName("Find rentals with return date"
            + " before specified date and actual return date is null")
    public void getAllByReturnDateBeforeAndActualReturnDateIsNull_correctDate_ok() {
        List<Rental> rentals = rentalRepository
                .getAllByReturnDateBeforeAndActualReturnDateIsNull(LocalDate.now());
        assertFalse(rentals.isEmpty(), "Rentals should not be empty");
        assertEquals(1, rentals.size());
    }

    @Test
    @DisplayName("Find rental by id with car")
    public void findByIdWithCar_correctId_returnTrue() {
        Rental rental = rentalRepository.findAll().get(0);
        Optional<Rental> foundRental = rentalRepository.findByIdWithCar(rental.getId());
        assertTrue(foundRental.isPresent(), "Rental should be present");
        assertEquals(rental.getId(), foundRental.get().getId());
        assertNotNull(foundRental.get().getCar(), "Car should be present in the rental");
    }
}
