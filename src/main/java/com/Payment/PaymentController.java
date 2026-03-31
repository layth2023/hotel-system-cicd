package com.Payment;

import com.PagedResponse;
import com.PaymentTransaction.PaymentTransactionResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PaymentResponseDTO create(@Valid @RequestBody PaymentRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PaymentResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PaymentResponseDTO markAsPaid(@PathVariable Long id) {
        return service.markAsPaid(id);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PaymentResponseDTO cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public List<PaymentResponseDTO> getByBooking(@PathVariable Long bookingId) {
        return service.findByBooking(bookingId);
    }

    // ==========================================================
    // Refund Endpoints
    // ==========================================================

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PaymentResponseDTO refund(@PathVariable Long id,
                                     @RequestParam(required = false) String reason) {
        return service.refund(id, reason);
    }

    @PostMapping("/{id}/partial-refund")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PaymentResponseDTO partialRefund(@PathVariable Long id,
                                            @Valid @RequestBody RefundRequestDTO refundRequest) {
        return service.partialRefund(id, refundRequest.getAmount(), refundRequest.getReason());
    }

    // ==========================================================
    // Transaction History
    // ==========================================================

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<PaymentTransactionResponseDTO> getPaymentTransactions(@PathVariable Long id) {
        return service.getPaymentTransactions(id);
    }

    // ==========================================================
    // Payment Summary & History
    // ==========================================================

    @GetMapping("/booking/{bookingId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PaymentSummaryDTO getBookingPaymentSummary(@PathVariable Long bookingId) {
        return service.getBookingPaymentSummary(bookingId);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<PaymentResponseDTO> getByUserId(@PathVariable Long userId, Pageable pageable) {
        Page<PaymentResponseDTO> page = service.findByUserId(userId, pageable);
        return PagedResponse.from(page);
    }
}