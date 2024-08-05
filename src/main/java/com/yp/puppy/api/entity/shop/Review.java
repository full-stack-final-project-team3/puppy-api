package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ShopReview") // 엔티티 이름 명시
@Table(name = "review")
public class Review {

    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
//    @GeneratedValue(generator = "uuid-generator")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id")
    private String id;

    @Column(nullable = false)
    private String reviewContent;

    @Column(nullable = false)
    private int rate; // 별점

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReviewPic> reviewPics = new ArrayList<>(); // 빈 리스트로 초기화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treats_id", nullable = false)
    @JsonIgnore
    private Treats treats;

    // 작성 날쨔 추가함
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}