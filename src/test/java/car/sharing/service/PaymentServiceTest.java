package car.sharing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import car.sharing.dto.payment.PaymentDto;
import car.sharing.dto.payment.PaymentRequestDto;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.exception.PaymentFailedException;
import car.sharing.exception.PendingPaymentsException;
import car.sharing.mapper.PaymentMapper;
import car.sharing.model.car.Car;
import car.sharing.model.car.CarType;
import car.sharing.model.payment.Payment;
import car.sharing.model.payment.PaymentType;
import car.sharing.model.payment.Status;
import car.sharing.model.rental.Rental;
import car.sharing.model.user.Role;
import car.sharing.model.user.RoleName;
import car.sharing.model.user.User;
import car.sharing.repository.PaymentRepository;
import car.sharing.repository.RentalRepository;
import car.sharing.service.impl.PaymentServiceImpl;
import car.sharing.service.telegram.TelegramNotificationService;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private TelegramNotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private PaymentRequestDto paymentRequestDto;
    private Rental rental;
    private Car car;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() throws MalformedURLException {
        car = new Car();
        car.setId(1L);
        car.setModel("Model 1");
        car.setBrand("Brand 1");
        car.setType(CarType.SUV);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(10.00));

        role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);

        user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setPassword("123456789encodedPassword");
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setRoles(Set.of(role));

        rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2024, 8, 10));
        rental.setReturnDate(LocalDate.of(2024, 8, 12));
        rental.setActualReturnDate(LocalDate.of(2024, 8, 14));
        rental.setCar(car);
        rental.setUser(user);

        payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Status.PENDING);
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl(new URL("http://example.com/sessionURL"));
        payment.setSessionId("sessionId1");
        payment.setAmountToPay(BigDecimal.valueOf(20));

        paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setStatus(payment.getStatus().name());
        paymentDto.setType(payment.getType().name());
        paymentDto.setRentalId(payment.getRental().getId());
        payment.setSessionUrl(payment.getSessionUrl());
        payment.setSessionId(payment.getSessionId());
        paymentDto.setAmountToPay(payment.getAmountToPay());
    }

    @Test
    @DisplayName("Find all payments by user ID")
    void findAllByUserId_WithValidUserId_ReturnPaymentList() {
        when(paymentRepository.findAllByRentalUserId(1L)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        List<PaymentDto> paymentList = paymentService.findAllByUserId(1L);

        assertNotNull(paymentList);
        assertEquals(1, paymentList.size());
        verify(paymentRepository, times(1)).findAllByRentalUserId(1L);
    }

    @Test
    @DisplayName("Find all payments")
    void findAll_ReturnPaymentList() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        List<PaymentDto> paymentList = paymentService.findAll();

        assertNotNull(paymentList);
        assertEquals(1, paymentList.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Create payment with type FINE and valid data")
    void createPayment_WithTypeFine_ReturnPaymentDto() {
        paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRentalId(rental.getId());
        paymentRequestDto.setType(PaymentType.FINE.name());

        when(rentalRepository
                .findByIdWithCar(rental.getId())).thenReturn(Optional.of(rental));
        when(paymentRepository
                .existsByRentalUserIdAndStatus(anyLong(),
                        eq(Status.PENDING))).thenReturn(false);
        when(stripeService.checkout(any(Payment.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(paymentDto);

        PaymentDto actual = paymentService.createPayment(paymentRequestDto);

        assertNotNull(actual);
        assertEquals(payment.getId(), actual.getId());
        verify(stripeService, times(1)).checkout(any(Payment.class));
    }

    @Test
    @DisplayName("Create payment with type PAYMENT and valid data")
    void createPayment_WithTypePayment_ReturnPaymentDto() {
        paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRentalId(rental.getId());
        paymentRequestDto.setType(PaymentType.PAYMENT.name());

        when(rentalRepository
                .findByIdWithCar(rental.getId())).thenReturn(Optional.of(rental));
        when(paymentRepository
                .existsByRentalUserIdAndStatus(anyLong(),
                        eq(Status.PENDING))).thenReturn(false);
        when(stripeService.checkout(any(Payment.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(paymentDto);

        PaymentDto actual = paymentService.createPayment(paymentRequestDto);

        assertNotNull(actual);
        assertEquals(payment.getId(), actual.getId());
        assertEquals(PaymentType.PAYMENT.name(), actual.getType());
        verify(stripeService, times(1)).checkout(any(Payment.class));
    }

    @Test
    @DisplayName("Create payment with invalid rental ID throws EntityNotFoundException")
    void createPayment_WithInvalidRentalId_ThrowsEntityNotFoundException() {
        paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRentalId(99L);
        paymentRequestDto.setType(PaymentType.PAYMENT.name());

        when(rentalRepository.findByIdWithCar(paymentRequestDto
                .getRentalId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.createPayment(paymentRequestDto)
        );

        String expected = "Can`t find rental by id" + paymentRequestDto.getRentalId();
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Create payment with pending payments throws exception")
    void createPayment_WithPendingPayments_ThrowsPendingPaymentsException() {
        paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setRentalId(rental.getId());
        paymentRequestDto.setType(PaymentType.PAYMENT.name());

        when(rentalRepository
                .findByIdWithCar(rental.getId())).thenReturn(Optional.of(rental));
        when(paymentRepository
                .existsByRentalUserIdAndStatus(anyLong(),
                        eq(Status.PENDING))).thenReturn(true);

        assertThrows(PendingPaymentsException.class,
                () -> paymentService.createPayment(paymentRequestDto));
    }

    @Test
    @DisplayName("Get successful payment with valid session ID")
    void getSuccessfulPayment_WithValidSessionId_ReturnPaymentDto() {
        when(paymentRepository.getBySessionId("valid-session")).thenReturn(Optional.of(payment));
        when(stripeService.isSessionPaid("valid-session")).thenReturn(true);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        PaymentDto actual = paymentService.getSuccessfulPayment("valid-session");

        assertNotNull(actual);
        assertEquals(payment.getId(), actual.getId());
        verify(paymentRepository, times(1)).save(payment);
        verify(notificationService, times(1)).sendMessage(anyString());
    }

    @Test
    @DisplayName("Get successful payment with unpaid session throws exception")
    void getSuccessfulPayment_WithUnpaidSession_ThrowsPaymentFailedException() {
        when(paymentRepository.getBySessionId("unpaid-session")).thenReturn(Optional.of(payment));
        when(stripeService.isSessionPaid("unpaid-session")).thenReturn(false);

        assertThrows(PaymentFailedException.class,
                () -> paymentService.getSuccessfulPayment("unpaid-session"));
    }

    @Test
    @DisplayName("Get cancelled payment with valid session ID")
    void getCancelledPayment_WithValidSessionId_ReturnPaymentDto() {
        when(paymentRepository.getBySessionId("valid-session")).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        PaymentDto actual = paymentService.getCancelledPayment("valid-session");

        assertNotNull(actual);
        assertEquals(payment.getId(), actual.getId());
        verify(paymentRepository, times(1)).save(payment);
        verify(notificationService, times(1)).sendMessage(anyString());
    }

    @Test
    @DisplayName("Get payment with invalid session ID throws exception")
    void getPayment_WithInvalidSessionId_ThrowsEntityNotFoundException() {
        when(paymentRepository.getBySessionId("invalid-session")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.getSuccessfulPayment("invalid-session"));
    }
}
