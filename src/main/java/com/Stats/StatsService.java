package com.Stats;

import java.time.LocalDate;

public interface StatsService {

    BookingStatsDTO getBookingStats(LocalDate startDate, LocalDate endDate);

    RevenueStatsDTO getRevenueStats(LocalDate startDate, LocalDate endDate);

    OccupancyStatsDTO getOccupancyStats(LocalDate startDate, LocalDate endDate);

    ReviewStatsDTO getReviewStats(LocalDate startDate, LocalDate endDate);

    DashboardDTO getHotelDashboard(Long hotelId, LocalDate startDate, LocalDate endDate);
}
