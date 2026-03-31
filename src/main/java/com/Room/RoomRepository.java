package com.Room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    List<Room> findByHotelId(Long hotelId);

    Page<Room> findByHotelId(Long hotelId, Pageable pageable);

    List<Room> findByHotelIdAndActiveTrue(Long hotelId);

    @Query("""
           SELECT r FROM Room r
           WHERE r.hotel.id = :hotelId
           AND r.active = true
           AND r.available = true
           AND r.roomType.capacity >= :guests
           """)
    List<Room> findAvailableRoomsByHotel(
            @Param("hotelId") Long hotelId,
            @Param("guests") Integer guests
    );

    @Query("""
           SELECT DISTINCT r FROM Room r
           WHERE r.active = true
           AND r.available = true
           AND r.roomType.capacity >= :guests
           AND NOT EXISTS (
               SELECT b FROM com.Booking.Booking b
               WHERE b.room = r
               AND b.status NOT IN ('CANCELLED', 'NO_SHOW')
               AND b.checkInDate < :checkOutDate
               AND b.checkOutDate > :checkInDate
           )
           """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("guests") Integer guests
    );

    @Query("""
           SELECT DISTINCT r FROM Room r
           WHERE r.hotel.id = :hotelId
           AND r.active = true
           AND r.available = true
           AND r.roomType.capacity >= :guests
           AND NOT EXISTS (
               SELECT b FROM com.Booking.Booking b
               WHERE b.room = r
               AND b.status NOT IN ('CANCELLED', 'NO_SHOW')
               AND b.checkInDate < :checkOutDate
               AND b.checkOutDate > :checkInDate
           )
           """)
    List<Room> findAvailableRoomsByHotelAndDates(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("guests") Integer guests
    );
}
