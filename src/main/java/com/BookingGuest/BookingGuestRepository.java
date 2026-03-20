package com.BookingGuest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BookingGuest entity operations.
 */
@Repository
public interface BookingGuestRepository extends JpaRepository<BookingGuest, Long> {

    List<BookingGuest> findByBookingId(Long bookingId);

    Optional<BookingGuest> findByBookingIdAndPrimaryGuestTrue(Long bookingId);

    void deleteByBookingId(Long bookingId);
}
