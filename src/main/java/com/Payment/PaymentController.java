package com.Payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(
            @Valid @RequestBody PaymentRequestDTO dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<PaymentResponseDTO> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsPaid(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponseDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @GetMapping("/booking/{bookingId}")
    public List<PaymentResponseDTO> getByBooking(@PathVariable Long bookingId) {
        return service.findByBooking(bookingId);
    }
}