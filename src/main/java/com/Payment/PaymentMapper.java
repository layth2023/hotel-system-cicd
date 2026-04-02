package com.Payment;
import com.Booking.Booking;
import java.time.LocalDateTime;

public class PaymentMapper {

    private PaymentMapper(){}

    public static Payment toEntity(PaymentRequestDTO dto,
                                   Booking booking) {
        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setBooking(booking);
        return payment;
    }

    public static PaymentResponseDTO toDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getBooking().getId()
        );
    }
}