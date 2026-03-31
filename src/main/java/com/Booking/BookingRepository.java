package com.Booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByConfirmationNumber(String confirmationNumber);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    List<Booking> findByUserId(Long userId);

    Page<Booking> findByRoomId(Long roomId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    @Query("""
           SELECT b FROM Booking b
           WHERE b.room.hotel.id = :hotelId
           """)
    Page<Booking> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    @Query("""
           SELECT b FROM Booking b
           WHERE b.user.id = :userId
           AND b.checkInDate > :today
           AND b.status IN ('PENDING', 'CONFIRMED')
           ORDER BY b.checkInDate ASC
           """)
    Page<Booking> findUpcomingByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            Pageable pageable
    );

    @Query("""
           SELECT b FROM Booking b
           WHERE b.checkInDate = :today
           AND b.status = 'CONFIRMED'
           """)
    List<Booking> findTodayCheckIns(@Param("today") LocalDate today);

    @Query("""
           SELECT b FROM Booking b
           WHERE b.checkOutDate = :today
           AND b.status = 'CHECKED_IN'
           """)
    List<Booking> findTodayCheckOuts(@Param("today") LocalDate today);

    @Query("""
           SELECT b FROM Booking b
           WHERE b.room.id = :roomId
           AND b.status NOT IN ('CANCELLED', 'NO_SHOW')
           AND b.checkInDate < :checkOutDate
           AND b.checkOutDate > :checkInDate
           """)
    List<Booking> findConflictingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("""
           SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
           FROM Booking b
           WHERE b.room.id = :roomId
           AND b.status NOT IN ('CANCELLED', 'NO_SHOW')
           AND b.checkInDate < :checkOutDate
           AND b.checkOutDate > :checkInDate
           """)
    boolean existsConflictingBooking(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}