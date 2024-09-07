package car.sharing.service.impl;

import car.sharing.model.payment.Payment;
import car.sharing.model.payment.Status;
import car.sharing.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Profile("!test")
public class StripeServiceImpl implements StripeService {
    private static final String DOMAIN = "http://localhost:8080";
    private static final String SUCCESS_URL = "/payments/success?sessionId={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL = "/payments/cancel?sessionId={CHECKOUT_SESSION_ID}";

    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    @Transactional
    public Payment checkout(Payment payment) {
        SessionCreateParams.Builder params = new SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setExpiresAt(getExpirationTime())
                .setSuccessUrl(DOMAIN + SUCCESS_URL)
                .setCancelUrl(DOMAIN + CANCEL_URL)
                .addLineItem(
                    new SessionCreateParams.LineItem.Builder()
                        .setPriceData(
                                new SessionCreateParams.LineItem.PriceData.Builder()
                                        .setCurrency("usd")
                                        .setProductData(
                                                new SessionCreateParams
                                                        .LineItem.PriceData.ProductData.Builder()
                                                        .setName("Type: " + payment.getType())
                                                        .build())
                                        .setUnitAmount(payment.getAmountToPay()
                                                .multiply(BigDecimal.valueOf(100)).longValue())
                                        .build())
                        .setQuantity(1L)
                        .build());
        Session session;
        try {
            session = Session.create(params.build());
            payment.setSessionId(session.getId());
            payment.setSessionUrl(new URL(session.getUrl()));
        } catch (StripeException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return payment;
    }

    private long getExpirationTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = currentTime.plusHours(12);
        return expirationTime.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public boolean isSessionPaid(String sessionId) {
        try {
            return Objects.equals(Session.retrieve(sessionId).getPaymentStatus(),
                    Status.PAID.name().toLowerCase());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
