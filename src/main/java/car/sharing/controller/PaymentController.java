package car.sharing.controller;

import car.sharing.dto.payment.PaymentDto;
import car.sharing.dto.payment.PaymentRequestDto;
import car.sharing.model.user.User;
import car.sharing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for payments managing")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/admin")
    @Operation(summary = "Get all payments for admin",
            description = "Get a list of all payments for admin")
    public List<PaymentDto> findAllPayment() {
        return paymentService.findAll();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    @Operation(summary = "Get all payments by user id",
            description = "Get a list of all payments by user id")
    public List<PaymentDto> findAllByUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.findAllByUserId(user.getId());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    @Operation(summary = "Create payment session", description = "Create payment session")
    public PaymentDto createPaymentSession(@RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.createPayment(requestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/success")
    @Operation(summary = "Endpoint for successful stripe redirection",
            description = "Check successful Stripe payments")
    public String getSuccessPayment(@RequestParam String sessionId) {
        paymentService.getSuccessfulPayment(sessionId);
        return "Success";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/cancel")
    @Operation(summary = "Endpoint for cancel stripe redirection",
            description = "Return payment paused message")
    public String getCancelledPayment(@RequestParam String sessionId) {
        paymentService.getCancelledPayment(sessionId);
        return "Payment was cancelled. The session is available for the next 24 hours.";
    }
}
