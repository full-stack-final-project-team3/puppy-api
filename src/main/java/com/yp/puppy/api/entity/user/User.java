package com.yp.puppy.api.entity.user;



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

    @Column(nullable = false)
    @Setter
    private boolean emailVerified;

    @Column(nullable = false, unique = true)
    private String nickname;

    private LocalDate birthday;

    private boolean isAutoLogin;

    private String phoneNumber;

    private Integer point;

    private String profileUrl;

    private boolean hasDogInfo; // 회원가입때 강아지 정보 등록했는지?

    @CreationTimestamp
    private LocalDateTime createdAt; // 가입 시간

    private int warningCount; // 경고 누적 횟수

    private boolean isDeleted; // 탈퇴한 적이 있나?
    // false - 탈퇴 x,  true - 탈퇴한적 있음

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Dog> dogList = new ArrayList<>(); // 몇마리의 강아지를 키우고 있나? ???





//    private List<Favorite> wishHotelList; // 유저가 찜한 호텔 리스트
//
//    private List<Board> scrappedBoard; // 유저가 북마크 남긴 게시글 리스트
//
//    private List<Package> wishPackageList; // 유저의 쇼핑몰 장바구니
//
//    private List<Reservation> reservation; // 호텔 - 유저 간의 중간테이블
//
//    private List<Order> order; // 쇼핑몰 - 유저 간의 중간테이블
//
//    private List<Board> board; // 유저가 쓴 글 리스트
//
//    private List<Board> reportList; // ADMIN에게 들어온 신고 리스트

    @PrePersist // 컬럼의 default value 설정
    public void prePersist() {
        if (this.point == null) {
            this.point = 0;
        }
        if (this.profileUrl == null) {
            this.profileUrl = ""; // 기본 이미지 여기서 설정
        }
    }

    public void confirm(String password) {
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
}
