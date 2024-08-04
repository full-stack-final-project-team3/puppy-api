package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.ReviewSaveDto;
import com.yp.puppy.api.entity.shop.Review;
import com.yp.puppy.api.service.shop.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/shop/reviews")
@CrossOrigin(origins = "http://localhost:8888")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰를 생성하는 메소드
     *
     * @param reviewSaveDto 리뷰 정보를 담고 있는 DTO 객체
     * @return 생성된 리뷰 객체를 포함하는 HTTP 응답
     */
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewSaveDto reviewSaveDto) {
        log.info("Received ReviewSaveDto: {}", reviewSaveDto);

        try {
            // 리뷰를 저장하고 반환
            Review review = reviewService.saveReview(reviewSaveDto);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            log.error("Error creating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 모든 리뷰를 조회하는 메소드
     *
     * @return 모든 리뷰를 포함하는 HTTP 응답
     */
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        try {
            // 모든 리뷰를 조회하고 반환합니다.
            List<Review> reviews = reviewService.findAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 특정 ID로 리뷰를 조회하는 메소드
     *
     * @param id 조회할 리뷰의 ID
     * @return 조회된 리뷰를 포함하는 HTTP 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable String id) {
        try {
            Review review = reviewService.findReviewById(id);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 특정 ID의 리뷰를 업데이트하는 메소드
     *
     * @param id 리뷰의 ID
     * @param reviewSaveDto 업데이트할 리뷰 정보를 담고 있는 DTO 객체
     * @return 업데이트된 리뷰 객체를 포함하는 HTTP 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable String id, @RequestBody ReviewSaveDto reviewSaveDto) {
        try {
            Review review = reviewService.updateReview(id, reviewSaveDto);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 특정 ID의 리뷰를 삭제하는 메소드
     *
     * @param id 삭제할 리뷰의 ID
     * @return HTTP 응답 (삭제가 성공적으로 수행된 경우)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        try {
            // 특정 ID의 리뷰를 삭제
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
