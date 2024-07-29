package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.ReviewSaveDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.hotel.HotelReviewRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * HotelReviewService는 호텔 리뷰 엔티티에 대한 비즈니스 로직을 처리합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotelReviewService {

    private final HotelReviewRepository hotelReviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    // 리뷰 생성 중간처리
    public Review createReview(ReviewSaveDto dto) {
        log.info("리뷰 저장 중: {}", dto);

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다"));

        Review newReview = dto.toEntity();
        newReview.setUser(user);
        newReview.setHotel(hotel);
        newReview.setReviewDate(LocalDateTime.now());

        Review saveReview = hotelReviewRepository.save(newReview);
        log.info("리뷰가 저장되었습니다: {}", saveReview);
        return saveReview;
    }

    // 리뷰 조회 중간처리
    public List<Review> getAllReviews() {
        return hotelReviewRepository.findAll();
    }

    // 리뷰 수정 중간처리
    public Review updateReview(String id, ReviewSaveDto dto) {
        Review review = hotelReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다"));

        review.changeReview(dto);

        return hotelReviewRepository.save(review);
    }

    // 리뷰 삭제 중간처리
    public void deleteReview(String id) {
        Review review = hotelReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다"));
        hotelReviewRepository.delete(review);
    }
}
