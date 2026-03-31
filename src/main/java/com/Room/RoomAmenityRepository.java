package com.Room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RoomAmenity entity operations.
 */
@Repository
public interface RoomAmenityRepository extends JpaRepository<RoomAmenity, Long> {

    List<RoomAmenity> findByRoomId(Long roomId);

    Page<RoomAmenity> findByRoomId(Long roomId, Pageable pageable);

    Optional<RoomAmenity> findByRoomIdAndAmenityId(Long roomId, Long amenityId);

    boolean existsByRoomIdAndAmenityId(Long roomId, Long amenityId);

    void deleteByRoomIdAndAmenityId(Long roomId, Long amenityId);

    @Query("SELECT COALESCE(SUM(ra.pricePerUnit * ra.quantity), 0) FROM RoomAmenity ra WHERE ra.room.id = :roomId")
    BigDecimal calculateTotalAmenityPriceForRoom(Long roomId);

    @Query("SELECT ra FROM RoomAmenity ra JOIN FETCH ra.amenity WHERE ra.room.id = :roomId")
    List<RoomAmenity> findByRoomIdWithAmenity(Long roomId);
}
