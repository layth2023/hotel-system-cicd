package com.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Review entity operations.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByHotelIdAndApprovedTrue(Long hotelId, Pageable pageable);

    Page<Review> findByHotelId(Long hotelId, Pageable pageable);

    List<Review> findByUserId(Long userId);

    Page<Review> findByApprovedFalse(Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId AND r.approved = true")
    Double getAverageRatingByHotelId(Long hotelId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotel.id = :hotelId AND r.approved = true")
    Long countApprovedReviewsByHotelId(Long hotelId);

    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

    boolean existsByUserIdAndBookingId(Long userId, Long bookingId);
}
