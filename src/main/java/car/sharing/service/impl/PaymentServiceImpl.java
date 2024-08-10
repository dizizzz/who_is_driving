package car.sharing.service.impl;

import car.sharing.dto.payment.PaymentDto;
import car.sharing.dto.payment.PaymentRequestDto;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.exception.PaymentFailedException;
import car.sharing.exception.PendingPaymentsException;
import car.sharing.mapper.PaymentMapper;
import car.sharing.model.car.Car;
import car.sharing.model.payment.Payment;
import car.sharing.model.payment.PaymentType;
import car.sharing.model.payment.Status;
import car.sharing.model.rental.Rental;
import car.sharing.repository.PaymentRepository;
import car.sharing.repository.RentalRepository;
import car.sharing.service.PaymentService;
import car.sharing.service.StripeService;
import car.sharing.service.telegram.TelegramNotificationService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final RentalRepository rentalRepository;
    private final TelegramNotificationService notificationService;

    @Override
    public List<PaymentDto> findAllByUserId(Long userId) {
        return paymentRepository.findAllByRentalUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        Rental rental = rentalRepository.findByIdWithCar(requestDto.getRentalId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can`t find rental by id" + requestDto.getRentalId()));

        boolean isPendingPayments = paymentRepository
                .existsByRentalUserIdAndStatus(rental.getUser().getId(), Status.PENDING);
        if (isPendingPayments) {
            throw new PendingPaymentsException(
                    "Cannot create a new payment while there are pending payments"
            );
        }

        Car car = rental.getCar();
        BigDecimal dailyFee = car.getDailyFee();
        BigDecimal amountToPay;

        if (requestDto.getType().equals(PaymentType.FINE.name())) {
            LocalDateTime actualReturnDateTime = rental.getActualReturnDate().atStartOfDay();
            LocalDateTime returnDateTime = rental.getReturnDate().atStartOfDay();
            Duration duration = Duration.between(returnDateTime, actualReturnDateTime);
            long days = duration.toDays() + 1;

            amountToPay = dailyFee.multiply(BigDecimal.valueOf(days)).multiply(FINE_MULTIPLIER);
        } else {
            LocalDateTime rentalDateTime = rental.getRentalDate().atStartOfDay();
            LocalDateTime returnDateTime = rental.getReturnDate().atStartOfDay();
            Duration duration = Duration.between(rentalDateTime, returnDateTime);
            long days = duration.toDays() + 1;

            amountToPay = dailyFee.multiply(BigDecimal.valueOf(days));
        }

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmountToPay(amountToPay);
        payment.setStatus(Status.PENDING);
        payment.setType(PaymentType.valueOf(requestDto.getType()));
        payment = stripeService.checkout(payment);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDto getSuccessfulPayment(String sessionId) {
        Payment payment = paymentRepository.getBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment by session id" + sessionId));

        if (!stripeService.isSessionPaid(sessionId)) {
            throw new PaymentFailedException("Payment has not been completed successfully");
        }
        payment.setStatus(Status.PAID);
        paymentRepository.save(payment);

        String message = "The payment: " + payment.getId()
                + " was successfully paid.\n"
                + "Amount paid: " + payment.getAmountToPay() + " USD";
        notificationService.sendMessage(message);
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto getCancelledPayment(String sessionId) {
        Payment payment = paymentRepository.getBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment by session id" + sessionId));

        payment.setStatus(Status.CANCELED);
        paymentRepository.save(payment);

        String message = "Oops, something went wrong..\n"
                + "The payment: " + payment.getId()
                + " for the car has failed.\n"
                + "Please try again!\n"
                + "Amount to pay: " + payment.getAmountToPay() + " USD";
        notificationService.sendMessage(message);
        return paymentMapper.toDto(payment);
    }
}
