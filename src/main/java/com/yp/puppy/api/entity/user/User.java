package com.yp.puppy.api.entity.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.community.BoardSubReply;
import com.yp.puppy.api.entity.community.Like;
import com.yp.puppy.api.entity.hotel.Favorite;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Order;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
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

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String realName;

    @Column(length = 500)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false)
    @Setter
    private boolean emailVerified;

    //    @Column(nullable = false, unique = true, length = 20)
    @Setter
    private String nickname;

    private LocalDate birthday;

    private boolean autoLogin;

    @Column(length = 50)
    private String phoneNumber;

    private Integer point;

    @Column(length = 500000)
    private String profileUrl;

    private boolean hasDogInfo; // 회원가입때 강아지 정보 등록했는지?

    @CreationTimestamp
    private LocalDateTime createdAt; // 가입 시간

    private int warningCount; // 경고 누적 횟수

    private boolean isDeleted; // 탈퇴한 적이 있나?
    // false - 탈퇴 x,  true - 탈퇴한적 있음

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Dog> dogList = new ArrayList<>(); // 몇마리의 강아지를 키우고 있나?

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("user-reservation")
    private List<Reservation> reservation = new ArrayList<>(); // 호텔 - 유저 간의 중간테이블

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Favorite> wishHotelList; // 유저가 찜한 호텔 리스트

    @OneToOne
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Bundle> createdBundles; // 유저가 생성한 번들 리스트

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Board> board;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardReply> boardReplies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardSubReply> boardSubReplies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;


    public void addDog(Dog dog) {
        dogList.add(dog);
        dog.setUser(this);
    }



    @PrePersist // 컬럼의 default value 설정
    public void prePersist() {
        if (this.point == null) {
            this.point = 0;
        }
        if (this.profileUrl == null) {
            this.profileUrl = ""; // 기본 이미지 여기서 설정
        }
        if (this.nickname == null) {
            this.nickname = "user"; // 기본 닉네임 설정
        }
    }

    public void confirm(String password, String nickname) {
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.nickname = nickname;
    }




    // 객실 예약할때 포인트 입 출금 메서드
    public void withdrawalPoints (int amount) {
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