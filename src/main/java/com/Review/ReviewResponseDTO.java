package com.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Review operations.
 */
public interface ReviewService {

    ReviewResponseDTO create(ReviewRequestDTO dto, Long userId);

    ReviewResponseDTO getById(Long id);

    ReviewResponseDTO update(Long id, ReviewRequestDTO dto, Long userId);

    void delete(Long id, Long userId);

    Page<ReviewResponseDTO> getByHotel(Long hotelId, Pageable pageable);

    List<ReviewResponseDTO> getByUser(Long userId);

    ReviewResponseDTO approve(Long id);

    ReviewResponseDTO addResponse(Long id, String response);

    Page<ReviewResponseDTO> getPendingReviews(Pageable pageable);

    Double getAverageRating(Long hotelId);
}
