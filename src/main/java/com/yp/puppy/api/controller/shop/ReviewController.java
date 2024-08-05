package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.ReviewSaveDto;
import com.yp.puppy.api.entity.shop.Review;
import com.yp.puppy.api.service.shop.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/shop/reviews")
@CrossOrigin(origins = "http://localhost:8888")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestPart("reviewSaveDto") ReviewSaveDto reviewSaveDto,
            @RequestPart(value = "reviewPics", required = false) List<MultipartFile> reviewPics) {
        log.info("Received ReviewSaveDto: {}", reviewSaveDto);
        reviewSaveDto.setReviewPics(reviewPics);

        try {
            Review review = reviewService.saveReview(reviewSaveDto);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            log.error("Error creating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable String id,
            @RequestPart("reviewSaveDto") ReviewSaveDto reviewSaveDto,
            @RequestPart(value = "reviewPics", required = false) List<MultipartFile> reviewPics) {
        log.info("Received ReviewSaveDto: {}", reviewSaveDto);
        reviewSaveDto.setReviewPics(reviewPics);

        try {
            Review review = reviewService.updateReview(id, reviewSaveDto);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            log.error("Error updating review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        try {
            List<Review> reviews = reviewService.findAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/review-img/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("파일을 읽을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("파일을 불러오는 데 실패했습니다.", e);
        }
    }
}
