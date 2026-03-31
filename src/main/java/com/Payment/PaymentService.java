package com.Payment;

import com.PaymentTransaction.PaymentTransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Payment operations.
 */
public interface PaymentService {

    PaymentResponseDTO create(PaymentRequestDTO dto);

    PaymentResponseDTO getById(Long id);

    PaymentResponseDTO markAsPaid(Long paymentId);

    PaymentResponseDTO markAsPaid(Long paymentId, String transactionId);

    PaymentResponseDTO cancel(Long paymentId);

    PaymentResponseDTO refund(Long paymentId, String reason);

    PaymentResponseDTO partialRefund(Long paymentId, BigDecimal amount, String reason);

    List<PaymentResponseDTO> findByBooking(Long bookingId);

    PaymentSummaryDTO getBookingPaymentSummary(Long bookingId);

    List<PaymentTransactionResponseDTO> getPaymentTransactions(Long paymentId);

    Page<PaymentResponseDTO> findByUserId(Long userId, Pageable pageable);
}
