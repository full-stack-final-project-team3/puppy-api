package com.yp.puppy.api.entity.hotel;

import com.yp.puppy.api.entity.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString(exclude = {"user", "room", "hotel"})
@EqualsAndHashCode(of ="reviewId")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "review_id")
    private String reviewId; // 리뷰 아이디

    @Column(nullable = false)
    private String content; // 리뷰 내용

    @Column(nullable = false)
    private int rating; // 평점

    @Column(nullable = false)
    private LocalDateTime createdAt; // 작성 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // 해당 호텔

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // 해당 객실
}
