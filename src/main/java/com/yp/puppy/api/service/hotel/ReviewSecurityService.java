package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.repository.hotel.HotelReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewSecurityService {

    private final HotelReviewRepository reviewRepository;

    public boolean isOwner(Authentication authentication, String reviewId) {
        String userId = authentication.getName();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다"));
        return review.getUser().getId().equals(userId);
    }
}
