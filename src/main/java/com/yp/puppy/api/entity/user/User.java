package com.yp.puppy.api.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yp.puppy.api.entity.community.*;
import com.yp.puppy.api.entity.hotel.Favorite;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Order;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude =
                {"profileUrl", "cart", "orders",
                "hotelReviews", "board", "likes", "reservation",
                "wishHotelList", "createdBundles", "boardReplies",
                "boardSubReplies", "dogList"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id")
    private String id;

    @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Setter
    private String realName;

    @Column(length = 500)
    @Setter
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false)
    @Setter
    private boolean emailVerified;

    @Setter
    @ColumnDefault("'user'")
    private String nickname;

    private LocalDate birthday;

    @Setter
    private String address;

    private boolean autoLogin;

    @Column(length = 50)
    @Setter
    private String phoneNumber;

    private Integer point;

    @Column(length = 500000)
    @Setter
    private String profileUrl;

    @Setter
    private String provider;

    private boolean hasDogInfo;

    @Setter
    @ColumnDefault("0")
    private int noticeCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private int warningCount;

    private boolean isDeleted;

    @Setter
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @Builder.Default
    @JsonManagedReference("user-dogs")
    private List<Dog> dogList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("user-reservations")
    private List<Reservation> reservation = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Favorite> wishHotelList;

    @OneToOne
    @JoinColumn(name = "cart_id")
    @Setter
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Bundle> createdBundles; // 유저가 생성한 번들 리스트


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Board> board;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardReply> boardReplies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardSubReply> boardSubReplies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-reviews")
    private List<Review> hotelReviews;

    // 알림 테이블 만듬. (0801)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    @JsonManagedReference("user-notices")
    private List<UserNotice> userNotices = new ArrayList<>();

    public void addDog(Dog dog) {
        this.dogList.add(dog);
        dog.setUser(this);
    }

    public void removeDog(Dog dog) {
        this.dogList.remove(dog);
        dog.setUser(null);
    }


    @PrePersist // 컬럼의 default value 설정
    public void prePersist() {
        if (this.point == null) {
            this.point = 0;
        }
        if (this.profileUrl == null) {
            // 기본 이미지 여기서 설정 - 0730 다시 수정
            this.profileUrl = "data:image/png;base64,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";
        }
    }
    public void confirm(String password, String nickname) {
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.nickname = nickname;
        //  0730 다시 수정
        this.profileUrl = "data:image/png;base64,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";
    }

    // 객실 예약할때 포인트 입 출금 메서드

    public void withdrawalPoints(int amount) {
        log.info("현재 포인트: {}, 차감할 포인트: {}", this.point, amount);
        if (this.point < amount) {
            throw new IllegalArgumentException("돈이 모자라~~");
        }
        this.point -= amount;
        log.info("포인트 차감 후 현재 포인트: {}", this.point);
    }

    public void addPoints(int amount) {
        log.info("포인트 추가 전: {}, 추가할 포인트: {}", this.point, amount);
        this.point += amount;
        log.info("포인트 추가 후: {}", this.point);
    }

}

