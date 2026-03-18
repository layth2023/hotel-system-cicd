package com.RoomAvailability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RoomAvailability entity operations.
 */
@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    Optional<RoomAvailability> findByRoomIdAndDate(Long roomId, LocalDate date);

    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.id = :roomId AND ra.date BETWEEN :startDate AND :endDate AND ra.available = true")
    List<RoomAvailability> findAvailableDates(Long roomId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(ra) FROM RoomAvailability ra WHERE ra.room.id = :roomId AND ra.date BETWEEN :startDate AND :endDate AND ra.available = false")
    long countUnavailableDates(Long roomId, LocalDate startDate, LocalDate endDate);

    void deleteByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);
}
