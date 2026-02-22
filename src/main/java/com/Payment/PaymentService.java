package com.Payment;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO create(PaymentRequestDTO dto);

    PaymentResponseDTO markAsPaid(Long paymentId);

    PaymentResponseDTO cancel(Long paymentId);

    List<PaymentResponseDTO> findByBooking(Long bookingId);
}