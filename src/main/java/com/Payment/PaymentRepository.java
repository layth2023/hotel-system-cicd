package com.Payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBookingId(Long bookingId);

    @Query("""
           SELECT p FROM Payment p
           WHERE p.booking.user.id = :userId
           ORDER BY p.createdAt DESC
           """)
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);
}