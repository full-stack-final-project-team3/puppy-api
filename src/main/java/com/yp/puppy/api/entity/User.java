package com.yp.puppy.api.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "user")
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

    @Setter
    @Column(nullable = false)
    private boolean emailVerified;

    private String nickname;

    private LocalDate birthday;

    private boolean isAutoLogin;

    private String phoneNumber;

    private int point;

    private String profileUrl;

    private boolean hasDogInfo; // 회원가입때 강아지 정보 등록했는지?

    private LocalDateTime createdAt; // 가입 시간

    private int WarningCount; // 경고 누적 횟수

    private boolean isDeleted; // 탈퇴한 적이 있나?
    // false - 탈퇴 x,  true - 탈퇴한적 있음

//    private List<Favorite> wishHotelList; // 유저가 찜한 호텔 리스트
//
//    private List<Board> scrappedBoard; // 유저가 북마크 남긴 게시글 리스트
//
//    private List<Package> wishPackageList; // 유저의 쇼핑몰 장바구니
//
//    private List<Dog> dogList; // 몇마리의 강아지를 키우고 있나? ???
//
//    private List<Reservation> reservation; // 호텔 - 유저 간의 중간테이블
//
//    private List<Order> order; // 쇼핑몰 - 유저 간의 중간테이블
//
//    private List<Board> board; // 유저가 쓴 글 리스트
//
//    private List<Board> reportList; // ADMIN에게 들어온 신고 리스트


    public void confirm(String password) {
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
}
