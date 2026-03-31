package com.Stats;

import java.math.BigDecimal;
import java.util.Map;

public class ReviewStatsDTO {

    private long totalReviews;
    private long approvedReviews;
    private long pendingReviews;
    private BigDecimal averageRating;
    private Map<Integer, Long> reviewsByRating;
    private Map<String, BigDecimal> averageRatingByHotel;

    public ReviewStatsDTO() {}

    // Getters and Setters
    public long getTotalReviews() { return totalReviews; }
    public void setTotalReviews(long totalReviews) { this.totalReviews = totalReviews; }

    public long getApprovedReviews() { return approvedReviews; }
    public void setApprovedReviews(long approvedReviews) { this.approvedReviews = approvedReviews; }

    public long getPendingReviews() { return pendingReviews; }
    public void setPendingReviews(long pendingReviews) { this.pendingReviews = pendingReviews; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public Map<Integer, Long> getReviewsByRating() { return reviewsByRating; }
    public void setReviewsByRating(Map<Integer, Long> reviewsByRating) { this.reviewsByRating = reviewsByRating; }

    public Map<String, BigDecimal> getAverageRatingByHotel() { return averageRatingByHotel; }
    public void setAverageRatingByHotel(Map<String, BigDecimal> averageRatingByHotel) { this.averageRatingByHotel = averageRatingByHotel; }
}
