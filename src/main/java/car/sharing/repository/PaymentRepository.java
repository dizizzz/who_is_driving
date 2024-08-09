package car.sharing.repository;

import car.sharing.model.payment.Payment;
import car.sharing.model.payment.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByRentalUserId(Long userId);

    Optional<Payment> getBySessionId(String sessionId);

    boolean existsByRentalUserIdAndStatus(Long userId, Status status);

    boolean existsByRentalIdAndStatus(Long rentalId, Status status);
}
