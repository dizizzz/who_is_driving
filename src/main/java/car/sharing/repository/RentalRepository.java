package car.sharing.repository;

import car.sharing.model.rental.Rental;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @EntityGraph(attributePaths = "car")
    List<Rental> getAllByUserIdAndActualReturnDateIsNull(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "car")
    List<Rental> getAllByUserIdAndActualReturnDateIsNotNull(Long userId, Pageable pageable);

    List<Rental> getAllByReturnDateBeforeAndActualReturnDateIsNull(LocalDate date);
}
