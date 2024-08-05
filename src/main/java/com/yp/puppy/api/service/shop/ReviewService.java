package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.ReviewSaveDto;
import com.yp.puppy.api.entity.shop.Review;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.ReviewRepository;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 리뷰를 저장하는 메소드
     *
     * @param reviewSaveDto 리뷰 정보를 담고 있는 DTO 객체
     * @return 저장된 리뷰 객체
     */
    public Review saveReview(ReviewSaveDto reviewSaveDto) {
        // 로그를 통해서 유저랑 간식 아이디를 확잉
        log.info("Looking for userId: {} and treatsId: {}", reviewSaveDto.getUserId(), reviewSaveDto.getTreatsId());

        // 유저 아이디랑 간식 아이디가 비어있는지 확인하거 비어있다면 예외를 던짐
        if (reviewSaveDto.getUserId() == null || reviewSaveDto.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is missing");
        }
        if (reviewSaveDto.getTreatsId() == null || reviewSaveDto.getTreatsId().trim().isEmpty()) {
            throw new IllegalArgumentException("Treats ID is missing");
        }

        //유저랑 간식을 디비에서 조회함
        Optional<User> userOptional = userRepository.findById(reviewSaveDto.getUserId());
        Optional<Treats> treatsOptional = treatsRepository.findById(reviewSaveDto.getTreatsId());

        // 유저랑 간식이 존재하는지 확인하거 로그를 남ㄱ
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

        //유저랑 간식이 존재? 있으믄 리뷰 생성해서 저장함
        if (userOptional.isPresent() && treatsOptional.isPresent()) {
            User user = userOptional.get();
            Treats treats = treatsOptional.get();

            Review review = Review.builder()
                    .reviewContent(reviewSaveDto.getReviewContent())  // 리뷰 내용 설정
                    .rate(reviewSaveDto.getRate())  // 별점 설정
                    .user(user)  // 리뷰를 작성한 사용자 설정
                    .treats(treats)  // 리뷰를 남긴 간식 설정
                    .createdAt(LocalDateTime.now())  // 작성 일자 설정
                    .build();

            return reviewRepository.save(review);  //리뷰를 디비에 저장하거 반환
        } else {
            throw new IllegalArgumentException("잘못된 유저 아이디 또는 간식 아이디");
        }
    }

    /**
     * 모든 리뷰를 조회하는 메소드
     *
     * @return 모든 리뷰의 리스트
     */
    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    /**
     * 특정 ID로 리뷰를 조회하는 메소드
     *
     * @param id 조회할 리뷰의 ID
     * @return 해당 ID의 리뷰 객체, 없으면 null
     */
    public Review findReviewById(String id) {
        return reviewRepository.findById(id).orElse(null);
    }

    /**
     * 특정 ID의 리뷰를 업데이트하는 메소드
     *
     * @param id 리뷰의 ID
     * @param reviewSaveDto 업데이트할 리뷰 정보를 담고 있는 DTO 객체
     * @return 업데이트된 리뷰 객체
     */
    public Review updateReview(String id, ReviewSaveDto reviewSaveDto) {
        // 리뷰를 조회
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        // 리뷰가 있으면 내용을 업데이트 후 저장
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setReviewContent(reviewSaveDto.getReviewContent());  // 리뷰 내용 업데이트
            review.setRate(reviewSaveDto.getRate());  // 별점 업데이트
            return reviewRepository.save(review);  // 업데이트된 리뷰 저장
        } else {
            throw new IllegalArgumentException("Invalid reviewId");
        }
    }

    /**
     * 특정 ID의 리뷰를 삭제하는 메소드
     *
     * @param id 삭제할 리뷰의 ID
     */
    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    /**
     * 사용자가 특정 간식을 구매했는지 확인하는 메소드
     *
     * @param userId 사용자의 ID
     * @param treatsId 간식의 ID
     * @return 사용자가 해당 간식을 구매했으면 true, 아니면 false
     */
    public boolean userHasPurchasedTreat(String userId, String treatsId) {

        return true;  // 현재는 항상 true 반환
    }

    /**
     * 특정 ID로 간식을 조회하는 메소드
     *
     * @param id 조회할 간식의 ID
     * @return 해당 ID의 간식 객체, 없으면 null
     */
    public Treats findTreatById(String id) {
        return treatsRepository.findById(id).orElse(null);
    }

    /**
     * 모든 간식을 조회하는 메소드
     *
     * @return 모든 간식의 리스트
     */
    public List<Treats> findAllTreats() {
        return treatsRepository.findAll();
    }
}