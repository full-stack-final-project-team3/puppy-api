package com.yp.puppy.api.entity.hotel;

import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Setter
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
        updateImages(dto.getHotelImages());

    }

    public void updateImages(List<HotelImage> newImages) {
        // 이미지 식별자 즉 uri 를 사용해 매핑
        Map<String, HotelImage> existingImages = this.images
                .stream()
                .collect(Collectors.toMap(HotelImage::getHotelImgUri, image -> image));

        List<HotelImage> updatedImages = new ArrayList<>();

        for (HotelImage newImage : newImages) {
            // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
            if (existingImages.containsKey(newImage.getHotelImgUri())) {
                // 이미 존재하는 이미지를 유지
                updatedImages.add(existingImages.get(newImage.getHotelImgUri()));
            } else {
                // 새 이미지 추가
                updatedImages.add(newImage);
                newImage.setHotel(this); // 관계 설정
            }
        }

        // 기존 리스트를 업데이트된 리스트로 교체
        this.images.clear();
        this.images.addAll(updatedImages);
    }


}
