package com.Review;

import com.Booking.Booking;
import com.Booking.BookingNotFoundException;
import com.Booking.BookingRepository;
import com.Hotel.Hotel;
import com.Hotel.HotelNotFoundException;
import com.Hotel.HotelRepository;
import com.Notification.NotificationService;
import com.Room.Room;
import com.Room.RoomNotFoundException;
import com.Room.RoomRepository;
import com.User.User;
import com.User.UserNotFoundException;
import com.User.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Review operations.
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    private final NotificationService notificationService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             HotelRepository hotelRepository,
                             RoomRepository roomRepository,
                             BookingRepository bookingRepository,
                             ReviewMapper reviewMapper,
                             NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.reviewMapper = reviewMapper;
        this.notificationService = notificationService;
    }

    @Override
    public ReviewResponseDTO create(ReviewRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Validate that either hotelId or roomId is provided
        if (dto.getHotelId() == null && dto.getRoomId() == null) {
            throw new IllegalArgumentException("Either hotelId or roomId must be provided");
        }

        Review review = new Review();
        review.setUser(user);

        // Handle hotel review
        if (dto.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(dto.getHotelId())
                    .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + dto.getHotelId()));

            // Check if user already reviewed this hotel
            if (reviewRepository.existsByUserIdAndHotelId(userId, dto.getHotelId())) {
                throw new ReviewAlreadyExistsException("You have already reviewed this hotel");
            }
            review.setHotel(hotel);
        }

        // Handle room review
        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RoomNotFoundException(dto.getRoomId()));

            // Check if user already reviewed this room
            if (reviewRepository.existsByUserIdAndRoomId(userId, dto.getRoomId())) {
                throw new ReviewAlreadyExistsException("You have already reviewed this room");
            }
            review.setRoom(room);

            // If room is provided but hotel is not, set the hotel from the room
            if (dto.getHotelId() == null) {
                review.setHotel(room.getHotel());
            }
        }

        review.setRating(dto.getRating());
        review.setTitle(dto.getTitle());
        review.setComment(dto.getComment());
        review.setCleanlinessRating(dto.getCleanlinessRating());
        review.setServiceRating(dto.getServiceRating());
        review.setLocationRating(dto.getLocationRating());
        review.setValueRating(dto.getValueRating());

        // If booking is provided, link it and mark as verified
        if (dto.getBookingId() != null) {
            Booking booking = bookingRepository.findById(dto.getBookingId())
                    .orElseThrow(() -> new BookingNotFoundException(dto.getBookingId()));
            review.setBooking(booking);
            review.setVerified(true);
        }

        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        return reviewMapper.toResponseDTO(review);
    }

    @Override
    public ReviewResponseDTO update(Long id, ReviewRequestDTO dto, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        // Only the owner can update the review
        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only update your own reviews");
        }

        reviewMapper.updateEntity(review, dto);
        Review updated = reviewRepository.save(review);
        return reviewMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        // Only the owner or admin can delete
        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getByHotel(Long hotelId, Pageable pageable) {
        return reviewRepository.findByHotelIdAndApprovedTrue(hotelId, pageable)
                .map(reviewMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getByUser(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(reviewMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDTO approve(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        review.setApproved(true);
        Review updated = reviewRepository.save(review);

        // Send notification
        notificationService.notifyReviewApproved(updated);

        return reviewMapper.toResponseDTO(updated);
    }

    @Override
    public ReviewResponseDTO addResponse(Long id, String response) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        review.setResponse(response);
        review.setResponseDate(LocalDateTime.now());
        Review updated = reviewRepository.save(review);

        // Send notification
        notificationService.notifyReviewResponded(updated);

        return reviewMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByApprovedFalse(pageable)
                .map(reviewMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long hotelId) {
        return reviewRepository.getAverageRatingByHotelId(hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getByRoom(Long roomId, Pageable pageable) {
        return reviewRepository.findByRoomIdAndApprovedTrue(roomId, pageable)
                .map(reviewMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByRoom(Long roomId) {
        return reviewRepository.getAverageRatingByRoomId(roomId);
    }
}
