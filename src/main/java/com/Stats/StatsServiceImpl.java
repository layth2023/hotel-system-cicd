package com.Stats;

import com.Booking.Booking;
import com.Booking.BookingRepository;
import com.Booking.BookingStatus;
import com.Hotel.Hotel;
import com.Hotel.HotelNotFoundException;
import com.Hotel.HotelRepository;
import com.Payment.Payment;
import com.Payment.PaymentRepository;
import com.Payment.PaymentStatus;
import com.Review.Review;
import com.Review.ReviewRepository;
import com.Room.Room;
import com.Room.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;

    public StatsServiceImpl(BookingRepository bookingRepository,
                            PaymentRepository paymentRepository,
                            RoomRepository roomRepository,
                            HotelRepository hotelRepository,
                            ReviewRepository reviewRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public BookingStatsDTO getBookingStats(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> !b.getCreatedAt().toLocalDate().isBefore(start) &&
                             !b.getCreatedAt().toLocalDate().isAfter(end))
                .toList();

        BookingStatsDTO stats = new BookingStatsDTO();
        stats.setTotalBookings(bookings.size());
        stats.setPendingBookings(countByStatus(bookings, BookingStatus.PENDING));
        stats.setConfirmedBookings(countByStatus(bookings, BookingStatus.CONFIRMED));
        stats.setCheckedInBookings(countByStatus(bookings, BookingStatus.CHECKED_IN));
        stats.setCheckedOutBookings(countByStatus(bookings, BookingStatus.CHECKED_OUT));
        stats.setCancelledBookings(countByStatus(bookings, BookingStatus.CANCELLED));
        stats.setNoShowBookings(countByStatus(bookings, BookingStatus.NO_SHOW));

        // Group by status
        Map<String, Long> byStatus = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getStatus().name(), Collectors.counting()));
        stats.setBookingsByStatus(byStatus);

        // Group by hotel
        Map<String, Long> byHotel = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getRoom().getHotel().getName(), Collectors.counting()));
        stats.setBookingsByHotel(byHotel);

        return stats;
    }

    @Override
    public RevenueStatsDTO getRevenueStats(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null &&
                             !p.getCreatedAt().toLocalDate().isBefore(start) &&
                             !p.getCreatedAt().toLocalDate().isAfter(end))
                .toList();

        RevenueStatsDTO stats = new RevenueStatsDTO();
        stats.setTotalPayments(payments.size());

        BigDecimal totalRevenue = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID || p.getStatus() == PaymentStatus.PARTIALLY_REFUNDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);

        BigDecimal totalRefunds = payments.stream()
                .filter(p -> p.getRefundAmount() != null)
                .map(Payment::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRefunds(totalRefunds);

        stats.setNetRevenue(totalRevenue.subtract(totalRefunds));

        stats.setSuccessfulPayments(payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .count());

        stats.setFailedPayments(payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count());

        // Group by hotel
        Map<String, BigDecimal> byHotel = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .collect(Collectors.groupingBy(
                        p -> p.getBooking().getRoom().getHotel().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));
        stats.setRevenueByHotel(byHotel);

        // Group by payment method
        Map<String, BigDecimal> byMethod = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentMethod().name(),
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));
        stats.setRevenueByPaymentMethod(byMethod);

        return stats;
    }

    @Override
    public OccupancyStatsDTO getOccupancyStats(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        List<Room> allRooms = roomRepository.findAll().stream()
                .filter(Room::isActive)
                .toList();

        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_IN ||
                             (b.getStatus() == BookingStatus.CONFIRMED &&
                              !b.getCheckInDate().isAfter(today) &&
                              !b.getCheckOutDate().isBefore(today)))
                .toList();

        OccupancyStatsDTO stats = new OccupancyStatsDTO();
        stats.setTotalRooms(allRooms.size());
        stats.setOccupiedRooms(activeBookings.size());
        stats.setAvailableRooms(allRooms.size() - activeBookings.size());

        if (allRooms.size() > 0) {
            BigDecimal rate = BigDecimal.valueOf(activeBookings.size())
                    .divide(BigDecimal.valueOf(allRooms.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            stats.setOccupancyRate(rate);
        } else {
            stats.setOccupancyRate(BigDecimal.ZERO);
        }

        // Group by hotel
        Map<String, BigDecimal> byHotel = new HashMap<>();
        Map<String, Long> roomsByHotel = allRooms.stream()
                .collect(Collectors.groupingBy(r -> r.getHotel().getName(), Collectors.counting()));
        Map<String, Long> occupiedByHotel = activeBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getRoom().getHotel().getName(), Collectors.counting()));

        for (String hotel : roomsByHotel.keySet()) {
            long total = roomsByHotel.get(hotel);
            long occupied = occupiedByHotel.getOrDefault(hotel, 0L);
            BigDecimal rate = total > 0 ?
                    BigDecimal.valueOf(occupied).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;
            byHotel.put(hotel, rate);
        }
        stats.setOccupancyByHotel(byHotel);

        return stats;
    }

    @Override
    public ReviewStatsDTO getReviewStats(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getCreatedAt() != null &&
                             !r.getCreatedAt().toLocalDate().isBefore(start) &&
                             !r.getCreatedAt().toLocalDate().isAfter(end))
                .toList();

        ReviewStatsDTO stats = new ReviewStatsDTO();
        stats.setTotalReviews(reviews.size());
        stats.setApprovedReviews(reviews.stream().filter(Review::isApproved).count());
        stats.setPendingReviews(reviews.stream().filter(r -> !r.isApproved()).count());

        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);
            stats.setAverageRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        } else {
            stats.setAverageRating(BigDecimal.ZERO);
        }

        // Group by rating
        Map<Integer, Long> byRating = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        stats.setReviewsByRating(byRating);

        // Average by hotel
        Map<String, BigDecimal> byHotel = reviews.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getHotel().getName(),
                        Collectors.collectingAndThen(
                                Collectors.averagingInt(Review::getRating),
                                avg -> BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
                        )
                ));
        stats.setAverageRatingByHotel(byHotel);

        return stats;
    }

    @Override
    public DashboardDTO getHotelDashboard(Long hotelId, LocalDate startDate, LocalDate endDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate today = LocalDate.now();

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setHotelId(hotelId);
        dashboard.setHotelName(hotel.getName());

        // Booking stats
        List<Booking> hotelBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getRoom().getHotel().getId().equals(hotelId))
                .filter(b -> !b.getCreatedAt().toLocalDate().isBefore(start) &&
                             !b.getCreatedAt().toLocalDate().isAfter(end))
                .toList();

        dashboard.setTotalBookings(hotelBookings.size());
        dashboard.setActiveBookings(hotelBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                .count());
        dashboard.setTodayCheckIns(hotelBookings.stream()
                .filter(b -> b.getCheckInDate().equals(today) && b.getStatus() == BookingStatus.CONFIRMED)
                .count());
        dashboard.setTodayCheckOuts(hotelBookings.stream()
                .filter(b -> b.getCheckOutDate().equals(today) && b.getStatus() == BookingStatus.CHECKED_IN)
                .count());

        // Revenue stats
        BigDecimal revenue = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking().getRoom().getHotel().getId().equals(hotelId))
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .filter(p -> p.getCreatedAt() != null &&
                             !p.getCreatedAt().toLocalDate().isBefore(start) &&
                             !p.getCreatedAt().toLocalDate().isAfter(end))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalRevenue(revenue);

        // Monthly revenue (last 30 days from today)
        BigDecimal monthlyRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking().getRoom().getHotel().getId().equals(hotelId))
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .filter(p -> p.getCreatedAt() != null &&
                             !p.getCreatedAt().toLocalDate().isBefore(today.minusDays(30)))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setMonthlyRevenue(monthlyRevenue);

        // Room stats
        List<Room> hotelRooms = roomRepository.findByHotelId(hotelId).stream()
                .filter(Room::isActive)
                .toList();
        dashboard.setTotalRooms(hotelRooms.size());

        long occupiedRooms = hotelBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_IN)
                .count();
        dashboard.setOccupiedRooms(occupiedRooms);

        if (hotelRooms.size() > 0) {
            BigDecimal rate = BigDecimal.valueOf(occupiedRooms)
                    .divide(BigDecimal.valueOf(hotelRooms.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            dashboard.setOccupancyRate(rate);
        } else {
            dashboard.setOccupancyRate(BigDecimal.ZERO);
        }

        // Review stats
        List<Review> hotelReviews = reviewRepository.findAll().stream()
                .filter(r -> r.getHotel().getId().equals(hotelId))
                .filter(Review::isApproved)
                .toList();
        dashboard.setTotalReviews(hotelReviews.size());

        if (!hotelReviews.isEmpty()) {
            double avgRating = hotelReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);
            dashboard.setAverageRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        } else {
            dashboard.setAverageRating(BigDecimal.ZERO);
        }

        return dashboard;
    }

    private long countByStatus(List<Booking> bookings, BookingStatus status) {
        return bookings.stream().filter(b -> b.getStatus() == status).count();
    }
}
