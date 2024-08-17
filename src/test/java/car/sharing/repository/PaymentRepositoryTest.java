package car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import car.sharing.model.payment.Payment;
import car.sharing.model.payment.Status;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/users/add-users.sql",
        "classpath:database/users/add-users-roles.sql",
        "classpath:database/cars/add-cars.sql",
        "classpath:database/rentals/add-rentals.sql",
        "classpath:database/payments/add-payments.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/payments/remove-payments.sql",
        "classpath:database/rentals/remove-rentals.sql",
        "classpath:database/cars/remove-cars.sql",
        "classpath:database/users/remove-users-roles.sql",
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Find the payment by correct rental user_id")
    void findAllByRentalUserId_correctUserId_returnTrue() {
        Long userId = 3L;
        List<Payment> payments = paymentRepository.findAllByRentalUserId(userId);
        assertEquals(1, payments.size());
    }

    @Test
    @DisplayName("Find the payment by correct session_id")
    void getBySessionId_correctSessionId_returnTrue() {
        String sessionId = "session1";
        Optional<Payment> payment = paymentRepository.getBySessionId(sessionId);
        assertTrue(payment.isPresent());
        assertEquals(sessionId, payment.get().getSessionId());
    }

    @Test
    @DisplayName("Verify the payment by correct rental user_id and status")
    void existsByRentalUserIdAndStatus_correctUserIdAndStatus_returnTrue() {
        Long userId = 3L;
        boolean exists = paymentRepository.existsByRentalUserIdAndStatus(userId, Status.PAID);
        assertTrue(exists);
    }

}
