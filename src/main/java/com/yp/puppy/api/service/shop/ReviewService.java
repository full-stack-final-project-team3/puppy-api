package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.ReviewSaveDto;
import com.yp.puppy.api.entity.shop.Review;
import com.yp.puppy.api.entity.shop.ReviewPic;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.ReviewPicRepository;
import com.yp.puppy.api.repository.shop.ReviewRepository;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TreatsRepository treatsRepository;
    private final ReviewPicRepository reviewPicRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Review saveReview(ReviewSaveDto reviewSaveDto) {
        log.info("Looking for userId: {} and treatsId: {}", reviewSaveDto.getUserId(), reviewSaveDto.getTreatsId());

        if (reviewSaveDto.getUserId() == null || reviewSaveDto.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is missing");
        }
        if (reviewSaveDto.getTreatsId() == null || reviewSaveDto.getTreatsId().trim().isEmpty()) {
            throw new IllegalArgumentException("Treats ID is missing");
        }

        Optional<User> userOptional = userRepository.findById(reviewSaveDto.getUserId());
        Optional<Treats> treatsOptional = treatsRepository.findById(reviewSaveDto.getTreatsId());

        if (userOptional.isPresent()) {
            log.info("유저 아이디 찾기 : {}", userOptional.get());
        } else {
            log.warn("유저 아이디를 찾지 못했습니다 : {}", reviewSaveDto.getUserId());
        }

        if (treatsOptional.isPresent()) {
            log.info("간식 아이디 찾기 : {}", treatsOptional.get());
        } else {
            log.warn("간식 아이디를 찾지 못했습니다 : {}", reviewSaveDto.getTreatsId());
        }

        if (userOptional.isPresent() && treatsOptional.isPresent()) {
            User user = userOptional.get();
            Treats treats = treatsOptional.get();

            Review review = Review.builder()
                    .reviewContent(reviewSaveDto.getReviewContent())
                    .rate(reviewSaveDto.getRate())
                    .user(user)
                    .treats(treats)
                    .createdAt(LocalDateTime.now())
                    .build();

            if (review.getReviewPics() == null) {
                review.setReviewPics(new ArrayList<>());
            }

            List<MultipartFile> reviewPics = reviewSaveDto.getReviewPics();
            if (reviewPics != null && !reviewPics.isEmpty()) {
                for (MultipartFile pic : reviewPics) {
                    String picName = saveImage(pic);
                    ReviewPic reviewPic = ReviewPic.builder()
                            .reviewPic(picName)
                            .review(review)
                            .build();
                    review.getReviewPics().add(reviewPic);
                }
            }

            return reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("잘못된 유저 아이디 또는 간식 아이디");
        }
    }

    public String saveImage(MultipartFile image) {
        try {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(uploadDir).resolve(newFilename);

            // 디렉토리가 존재하지 않으면 생성
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            // 경로 출력
            System.out.println("이미지 저장 경로 : " + path.toString());

            Files.write(path, image.getBytes());

            // 로그 출력
            log.info("이미지 저장 성공: {}", path.toString());

            return newFilename;
        } catch (IOException e) {
            log.error("이미지 저장 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage(), e);
        }
    }

    public Review updateReview(String id, ReviewSaveDto reviewSaveDto) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setReviewContent(reviewSaveDto.getReviewContent());
            review.setRate(reviewSaveDto.getRate());

            List<MultipartFile> reviewPics = reviewSaveDto.getReviewPics();
            if (reviewPics != null && !reviewPics.isEmpty()) {
                // 기존 이미지 삭제
                reviewPicRepository.deleteAll(review.getReviewPics());
                review.getReviewPics().clear();
                for (MultipartFile pic : reviewPics) {
                    String picName = saveImage(pic);
                    ReviewPic reviewPic = ReviewPic.builder()
                            .reviewPic(picName)
                            .review(review)
                            .build();
                    review.getReviewPics().add(reviewPic);
                }
            }

            return reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("Invalid reviewId");
        }
    }

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    public Review findReviewById(String id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    public boolean userHasPurchasedTreat(String userId, String treatsId) {
        return true;
    }

    public Treats findTreatById(String id) {
        return treatsRepository.findById(id).orElse(null);
    }

    public List<Treats> findAllTreats() {
        return treatsRepository.findAll();
    }
}
