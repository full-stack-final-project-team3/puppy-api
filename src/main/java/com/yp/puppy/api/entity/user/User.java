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

    @Setter
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
            this.profileUrl = "https://kyg323.cafe24.com/img/img/user1.png";
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
        this.profileUrl = "https://kyg323.cafe24.com/img/img/user1.png";
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

