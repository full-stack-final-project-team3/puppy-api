package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.dto.request.hotel.ReviewSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelReviewDetailDto;
import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.service.hotel.HotelReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@Slf4j
@RequiredArgsConstructor
public class HotelReviewController {

    private final HotelReviewService hotelReviewService;

    // 1. 리뷰 작성
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewSaveDto dto) {
        log.info("리뷰 작성 중: {}", dto);
        try {
            Review addReview = hotelReviewService.createReview(dto);
            return ResponseEntity.ok().body(addReview);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // 2. 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        try {
            hotelReviewService.deleteReview(reviewId);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "리뷰가 성공적으로 삭제되었습니다"));
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // 3. 리뷰 조회
    @GetMapping
    public ResponseEntity<List<HotelReviewDetailDto>> getReviewsByHotelId(@RequestParam String hotelId) {
        List<HotelReviewDetailDto> reviews = hotelReviewService.getReviewsByHotelId(hotelId)
                .stream()
                .map(HotelReviewDetailDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    // 4. 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable String reviewId, @RequestBody ReviewSaveDto dto) {
        try {
            Review updatedReview = hotelReviewService.updateReview(reviewId, dto);
            return ResponseEntity.ok().body(updatedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
