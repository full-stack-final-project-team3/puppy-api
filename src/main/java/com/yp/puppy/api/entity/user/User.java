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
                "wishHotelList", "createdBundles",
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

    @Setter
    private String detailAddress;

    private boolean autoLogin;

    @Column(length = 50)
    @Setter
    private String phoneNumber;

    @Setter
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

    @Setter
    private Long expensedInShop;

    @Setter
    private Long expensedInHotel;

    @OneToOne
    @JoinColumn(name = "cart_id")
    @Setter
    @JsonIgnore
    private Cart cart;

    @JsonIgnore //yj : StackOverflowError 때문에 추가
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
        if (this.expensedInHotel == null) {
            this.expensedInHotel = 0L;
        }
        if (this.expensedInShop == null) {
            this.expensedInShop = 0L;
        }
    }

    public void confirm(String password, String nickname, String address, String phoneNumber, String detailAddress) {
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.detailAddress = detailAddress;
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

