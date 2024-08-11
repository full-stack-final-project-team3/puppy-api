package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.ReviewSaveDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.hotel.HotelReviewRepository;
import com.yp.puppy.api.repository.hotel.ReservationRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotelReviewService {

    private final HotelReviewRepository hotelReviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    // 리뷰 생성 중간처리
    public Review createReview(ReviewSaveDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + dto.getUserId()));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다: " + dto.getHotelId()));
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + dto.getReservationId()));

        Review newReview = dto.toEntity();
        newReview.setUser(user);
        newReview.setHotel(hotel);
        newReview.setReservation(reservation); // 예약 정보 설정
        newReview.setReviewDate(LocalDateTime.now());

        Review savedReview = hotelReviewRepository.save(newReview);
        log.info("리뷰가 저장되었습니다: {}", savedReview);
        return savedReview;
    }

    // 리뷰 전체조회 중간처리 아마안쓸듯?
    public List<Review> getAllReviews() {
        return hotelReviewRepository.findAll();
    }

    // 리뷰 조회 중간처리
    public List<Review> getReviewsByHotelId(String reservationId) {
        return hotelReviewRepository.findByReservationReservationId(reservationId);
    }

    // 리뷰 수정 중간처리
    public Review updateReview(String id, ReviewSaveDto dto) {
        Review review = hotelReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + id));

        // 리뷰 작성자가 현재 사용자와 일치하는지 확인
        if (!review.getUser().getId().equals(dto.getUserId())) {
            throw new IllegalArgumentException("리뷰 수정 권한이 없습니다.");
        }

        review.changeReview(dto);
        return hotelReviewRepository.save(review);
    }

    // 리뷰 삭제 중간처리
    public void deleteReview(String id, String userId) {
        Review review = hotelReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + id));

        // 리뷰 작성자가 현재 사용자와 일치하는지 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("리뷰 삭제 권한이 없습니다.");
        }

        hotelReviewRepository.delete(review);
    }
}
