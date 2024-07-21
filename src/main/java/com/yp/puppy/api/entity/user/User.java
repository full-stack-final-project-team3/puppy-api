package com.yp.puppy.api.entity.user;

import com.yp.puppy.api.entity.hotel.Favorite;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.entity.shop.Order;
import lombok.*;
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

@Entity
@Table(name = "users")
public class User {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "user_id")
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

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
    @Setter
    private List<Dog> dogList = new ArrayList<>(); // 몇마리의 강아지를 키우고 있나? ???

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservation = new ArrayList<>(); // 호텔 - 유저 간의 중간테이블

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Favorite> wishHotelList; // 유저가 찜한 호텔 리스트

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

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

    public void addDog(Dog dog) {
        this.dogList.add(dog);
        dog.setUser(this);
    }
}