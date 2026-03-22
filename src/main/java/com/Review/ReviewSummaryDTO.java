package com.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor

public class ReviewSummaryDTO {

    private Long roomId;
    private Double averageRating;
    private Long reviewCount;

}