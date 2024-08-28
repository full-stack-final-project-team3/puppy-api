package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.ReviewSaveDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelReviewRepository;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.hotel.ReservationRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotelReviewService {

    private final HotelReviewRepository hotelReviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public Review createReview(ReviewSaveDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + dto.getHotelId()));
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + dto.getReservationId()));

        Review newReview = dto.toEntity();
        newReview.setUser(user);
        newReview.setHotel(hotel);
        newReview.setReservation(reservation);
        newReview.setReviewDate(LocalDateTime.now());

        return hotelReviewRepository.save(newReview);
    }

    public List<Review> getAllReviews() {
        return hotelReviewRepository.findAll();
    }

    public List<Review> getReviewsByReservationId(String reservationId) {
        return hotelReviewRepository.findByReservationReservationId(reservationId);
    }

    public List<Review> getReviewsByHotelId(String hotelId) {
        return hotelReviewRepository.findByHotelHotelId(hotelId);
    }

    public Review updateReview(String reviewId, ReviewSaveDto dto) {
        Review review = hotelReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        review.changeReview(dto);
        return hotelReviewRepository.save(review);
    }

    public void deleteReview(String reviewId) {
        Review review = hotelReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        hotelReviewRepository.delete(review);
    }
}
