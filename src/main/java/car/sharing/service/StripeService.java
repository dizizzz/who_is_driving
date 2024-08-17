package car.sharing.service;

import car.sharing.model.payment.Payment;

public interface StripeService {

    Payment checkout(Payment payment);

    boolean isSessionPaid(String sessionId);
}
