package com.Stats;

import java.util.Map;

public class BookingStatsDTO {

    private long totalBookings;
    private long pendingBookings;
    private long confirmedBookings;
    private long checkedInBookings;
    private long checkedOutBookings;
    private long cancelledBookings;
    private long noShowBookings;
    private Map<String, Long> bookingsByStatus;
    private Map<String, Long> bookingsByHotel;

    public BookingStatsDTO() {}

    // Getters and Setters
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getPendingBookings() { return pendingBookings; }
    public void setPendingBookings(long pendingBookings) { this.pendingBookings = pendingBookings; }

    public long getConfirmedBookings() { return confirmedBookings; }
    public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }

    public long getCheckedInBookings() { return checkedInBookings; }
    public void setCheckedInBookings(long checkedInBookings) { this.checkedInBookings = checkedInBookings; }

    public long getCheckedOutBookings() { return checkedOutBookings; }
    public void setCheckedOutBookings(long checkedOutBookings) { this.checkedOutBookings = checkedOutBookings; }

    public long getCancelledBookings() { return cancelledBookings; }
    public void setCancelledBookings(long cancelledBookings) { this.cancelledBookings = cancelledBookings; }

    public long getNoShowBookings() { return noShowBookings; }
    public void setNoShowBookings(long noShowBookings) { this.noShowBookings = noShowBookings; }

    public Map<String, Long> getBookingsByStatus() { return bookingsByStatus; }
    public void setBookingsByStatus(Map<String, Long> bookingsByStatus) { this.bookingsByStatus = bookingsByStatus; }

    public Map<String, Long> getBookingsByHotel() { return bookingsByHotel; }
    public void setBookingsByHotel(Map<String, Long> bookingsByHotel) { this.bookingsByHotel = bookingsByHotel; }
}
