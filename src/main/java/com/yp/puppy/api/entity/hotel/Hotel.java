package com.yp.puppy.api.entity.hotel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.response.hotel.ImageDto;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
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
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("hotel-reviews")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("hotel-reservations")
    private List<Reservation> reservations = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("hotel-images")
    private List<HotelImage> images = new ArrayList<>();

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

    public void updateImages(List<ImageDto> newImages) {
        Map<String, HotelImage> existingImages = this.images
                .stream()
                .collect(Collectors.toMap(HotelImage::getHotelImgUri, image -> image));

        List<HotelImage> updatedImages = new ArrayList<>();

        for (ImageDto newImageDto : newImages) {
            if (existingImages.containsKey(newImageDto.getHotelImgUri())) {
                updatedImages.add(existingImages.get(newImageDto.getHotelImgUri()));
            } else {
                HotelImage newImage = HotelImage.builder()
                        .type(newImageDto.getType())
                        .hotelImgUri(newImageDto.getHotelImgUri())
                        .hotel(this)
                        .build();
                updatedImages.add(newImage);
            }
        }

        this.images.clear();
        this.images.addAll(updatedImages);
    }
}

