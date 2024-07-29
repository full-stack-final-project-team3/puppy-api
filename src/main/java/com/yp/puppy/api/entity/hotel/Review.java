package com.yp.puppy.api.entity.hotel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yp.puppy.api.dto.request.hotel.ReviewSaveDto;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "HotelReview")
@Table(name = "hotel_review")
@ToString(exclude = {"user", "hotel"}) // 순환 참조를 피하기 위해 추가
public class Review {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id")
    private String id;

    @Column(nullable = false)
    private String reviewContent; // 내용

    @Column(nullable = false)
    private int rate; // 별점

    @Column(nullable = false)
    private LocalDateTime reviewDate; // 리뷰 작성 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user; // 리뷰 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference
    private Hotel hotel; // 리뷰가 작성된 호텔

    public void changeReview(ReviewSaveDto dto) {
        this.reviewContent = dto.getReviewContent();
        this.rate = dto.getRate();
        this.reviewDate = LocalDateTime.now();
    }
}
