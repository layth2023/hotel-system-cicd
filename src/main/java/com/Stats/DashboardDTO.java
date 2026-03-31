package com.Stats;

import java.math.BigDecimal;

public class DashboardDTO {

    private Long hotelId;
    private String hotelName;

    // Booking stats
    private long totalBookings;
    private long activeBookings;
    private long todayCheckIns;
    private long todayCheckOuts;

    // Revenue stats
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;

    // Occupancy stats
    private long totalRooms;
    private long occupiedRooms;
    private BigDecimal occupancyRate;

    // Review stats
    private BigDecimal averageRating;
    private long totalReviews;

    public DashboardDTO() {}

    // Getters and Setters
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getActiveBookings() { return activeBookings; }
    public void setActiveBookings(long activeBookings) { this.activeBookings = activeBookings; }

    public long getTodayCheckIns() { return todayCheckIns; }
    public void setTodayCheckIns(long todayCheckIns) { this.todayCheckIns = todayCheckIns; }

    public long getTodayCheckOuts() { return todayCheckOuts; }
    public void setTodayCheckOuts(long todayCheckOuts) { this.todayCheckOuts = todayCheckOuts; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }

    public long getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(long occupiedRooms) { this.occupiedRooms = occupiedRooms; }

    public BigDecimal getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(BigDecimal occupancyRate) { this.occupancyRate = occupancyRate; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public long getTotalReviews() { return totalReviews; }
    public void setTotalReviews(long totalReviews) { this.totalReviews = totalReviews; }
}
