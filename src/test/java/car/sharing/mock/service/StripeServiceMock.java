package car.sharing.mock.service;

import car.sharing.model.payment.Payment;
import car.sharing.service.StripeService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class StripeServiceMock implements StripeService {

    @Override
    public Payment checkout(Payment payment) {
        payment.setSessionId("mockSessionId");
        return payment;
    }

    @Override
    public boolean isSessionPaid(String sessionId) {
        return "mockSessionId".equals(sessionId);
    }
}
