package com.Review;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO create(ReviewRequestDTO dto);
    List<ReviewResponseDTO> getByRoom(Long roomId);
    ReviewSummaryDTO getRoomSummary(Long roomId);
    List<ReviewResponseDTO> getByHotel(Long hotelId);
}
