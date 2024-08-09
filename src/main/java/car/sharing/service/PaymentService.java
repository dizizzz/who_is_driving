package car.sharing.service;

import car.sharing.dto.payment.PaymentDto;
import car.sharing.dto.payment.PaymentRequestDto;
import java.util.List;

public interface PaymentService {
    List<PaymentDto> findAll();

    PaymentDto createPayment(PaymentRequestDto requestDto);

    List<PaymentDto> findAllByUserId(Long userId);

    PaymentDto getSuccessfulPayment(String sessionId);

    PaymentDto getCancelledPayment(String sessionId);

}
