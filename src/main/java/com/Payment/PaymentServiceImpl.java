package com.Payment;
import com.Booking.Booking;
import com.Booking.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public PaymentResponseDTO create(PaymentRequestDTO dto) {

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = PaymentMapper.toEntity(dto, booking);
        return PaymentMapper.toDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponseDTO markAsPaid(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.PAID);
        return PaymentMapper.toDTO(payment);
    }

    @Override
    public PaymentResponseDTO cancel(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.CANCELLED);
        return PaymentMapper.toDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> findByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(PaymentMapper::toDTO)
                .toList();
    }

}