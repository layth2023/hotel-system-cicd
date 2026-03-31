package com.Payment;

import com.Booking.Booking;
import com.Booking.BookingNotFoundException;
import com.Booking.BookingRepository;
import com.Notification.NotificationService;
import com.PaymentTransaction.PaymentTransactionRepository;
import com.PaymentTransaction.PaymentTransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              PaymentTransactionRepository transactionRepository,
                              NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Override
    public PaymentResponseDTO create(PaymentRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(dto.getBookingId()));

        Payment payment = PaymentMapper.toEntity(dto, booking);
        return PaymentMapper.toDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponseDTO markAsPaid(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        payment.markAsPaid(transactionId);
        Payment saved = paymentRepository.save(payment);

        // Send notification
        notificationService.notifyPaymentReceived(saved);

        return PaymentMapper.toDTO(saved);
    }

    @Override
    public PaymentResponseDTO markAsPaid(Long paymentId) {
        return markAsPaid(paymentId, "TXN" + System.currentTimeMillis());
    }

    @Override
    public PaymentResponseDTO cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new PaymentBadRequestException("Cannot cancel a paid payment. Use refund instead.");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        return PaymentMapper.toDTO(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(PaymentMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return PaymentMapper.toDTO(payment);
    }

    @Override
    public PaymentResponseDTO refund(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.isRefundable()) {
            throw new PaymentBadRequestException("Payment is not refundable");
        }

        payment.processRefund(payment.getAmount(), reason);
        Payment saved = paymentRepository.save(payment);

        // Send notification
        notificationService.notifyPaymentRefunded(saved);

        return PaymentMapper.toDTO(saved);
    }

    @Override
    public PaymentResponseDTO partialRefund(Long paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.isRefundable()) {
            throw new PaymentBadRequestException("Payment is not refundable");
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new PaymentBadRequestException("Refund amount cannot exceed payment amount");
        }

        // Calculate remaining refundable amount
        BigDecimal alreadyRefunded = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
        BigDecimal refundable = payment.getAmount().subtract(alreadyRefunded);

        if (amount.compareTo(refundable) > 0) {
            throw new PaymentBadRequestException("Refund amount exceeds remaining refundable amount: " + refundable);
        }

        payment.processRefund(alreadyRefunded.add(amount), reason);
        Payment saved = paymentRepository.save(payment);

        // Send notification
        notificationService.notifyPaymentRefunded(saved);

        return PaymentMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryDTO getBookingPaymentSummary(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        List<Payment> payments = paymentRepository.findByBookingId(bookingId);

        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID || p.getStatus() == PaymentStatus.PARTIALLY_REFUNDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunded = payments.stream()
                .filter(p -> p.getRefundAmount() != null)
                .map(Payment::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netPaid = totalPaid.subtract(totalRefunded);
        BigDecimal balanceDue = booking.getTotalPrice().subtract(netPaid);

        return new PaymentSummaryDTO(
                bookingId,
                booking.getTotalPrice(),
                totalPaid,
                totalRefunded,
                balanceDue,
                payments.size(),
                balanceDue.compareTo(BigDecimal.ZERO) <= 0
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponseDTO> getPaymentTransactions(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new PaymentNotFoundException(paymentId);
        }

        return transactionRepository.findByPaymentId(paymentId)
                .stream()
                .map(PaymentTransactionResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(PaymentMapper::toDTO);
    }
}