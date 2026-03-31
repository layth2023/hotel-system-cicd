package com.Review;

import org.springframework.stereotype.Component;

/**
 * Mapper for Review entity and DTOs.
 */
@Component
public class ReviewMapper {

    public ReviewResponseDTO toResponseDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        if (review.getHotel() != null) {
            dto.setHotelId(review.getHotel().getId());
            dto.setHotelName(review.getHotel().getName());
        }
        if (review.getRoom() != null) {
            dto.setRoomId(review.getRoom().getId());
            dto.setRoomNumber(review.getRoom().getRoomNumber());
        }
        if (review.getBooking() != null) {
            dto.setBookingId(review.getBooking().getId());
        }
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setVerified(review.isVerified());
        dto.setApproved(review.isApproved());
        dto.setResponse(review.getResponse());
        dto.setResponseDate(review.getResponseDate());
        dto.setCleanlinessRating(review.getCleanlinessRating());
        dto.setServiceRating(review.getServiceRating());
        dto.setLocationRating(review.getLocationRating());
        dto.setValueRating(review.getValueRating());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public void updateEntity(Review review, ReviewRequestDTO dto) {
        if (dto.getRating() != null) {
            review.setRating(dto.getRating());
        }
        if (dto.getTitle() != null) {
            review.setTitle(dto.getTitle());
        }
        if (dto.getComment() != null) {
            review.setComment(dto.getComment());
        }
        if (dto.getCleanlinessRating() != null) {
            review.setCleanlinessRating(dto.getCleanlinessRating());
        }
        if (dto.getServiceRating() != null) {
            review.setServiceRating(dto.getServiceRating());
        }
        if (dto.getLocationRating() != null) {
            review.setLocationRating(dto.getLocationRating());
        }
        if (dto.getValueRating() != null) {
            review.setValueRating(dto.getValueRating());
        }
    }
}
