package com.yp.puppy.api.entity.hotel;

import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Getter
@ToString(exclude = {"rooms", "reviews", "reservations", "images"})
@EqualsAndHashCode(of = "hotelId")
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "hotel_id")
    private String hotelId; // 호텔 아이디

    @Column(nullable = false)
    private String name; // 호텔 이름

    @Column(length = 1000)
    private String description; // 호텔 설명

    @Column(nullable = false)
    private String businessOwner; // 사업자 정보

    @Column(nullable = false)
    private String location; // 호텔 위치

    @Column(length = 1000)
    private String rulesPolicy; // 호텔 규칙

    @Column(length = 1000)
    private String cancelPolicy; // 취소 규정

    @Column(nullable = false)
    private long price; // 호텔 가격

    @Column(nullable = false)
    private String phoneNumber; // 호텔 전화번호

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User hotelUser;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>(); // 객실 목록

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>(); // 리뷰 목록

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>(); // 예약 목록

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HotelImage> images = new ArrayList<>(); // 이미지 목록


    public void changeHotel(HotelSaveDto dto) {

        this.name = dto.getName();
        this.description = dto.getDescription();
        this.businessOwner = dto.getBusinessOwner();
        this.location = dto.getLocation();
        this.rulesPolicy = dto.getRulesPolicy();
        this.cancelPolicy = dto.getCancelPolicy();
        this.price = dto.getPrice();
        this.phoneNumber = dto.getPhoneNumber();
        this.images = dto.getHotelImages();

    }

}
