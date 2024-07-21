package com.yp.puppy.api.entity.hotel;

import com.yp.puppy.api.dto.request.hotel.RoomSaveDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@ToString(exclude = {"reservations", "dogs", "hotel"})
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "roomId")
@Builder

@Entity
@Table(name = "rooms") // 테이블 이름을 복수형으로 수정
public class Room {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "room_id")
    private String roomId; // 객실 아이디 (UUID)

    @Column(nullable = false)
    private String name; // 객실 이름

    @Column(length = 1000)
    private String content; // 객실 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type; // 객실 타입 (소형견, 중형견, 대형견)

    @Column(nullable = false)
    private long price; // 객실 가격

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User roomUser;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // 객실이 속한 호텔

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Dog> dogs = new ArrayList<>(); // 애완견 목록

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reservation> reservations =  new ArrayList<>(); // 객실의 예약 목록

    @Setter
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HotelImage> images = new ArrayList<>(); // 이미지 목록


    public void changeRoom(RoomSaveDto dto){
        this.name = dto.getName();
        this.content = dto.getContent();
        this.type = dto.getType();
        this.price = dto.getPrice();
        updateImages(dto.getRoomImage());

    }

    private void updateImages(List<HotelImage> roomImage) {
        // 이미지 식별자 즉 uri 를 사용해 매핑
        Map<String, HotelImage> imageMap = this.images
                .stream()
                .collect(Collectors.toMap(HotelImage::getHotelImgUri, image -> image));

        List<HotelImage> updatedImages = new ArrayList<>();

        for (HotelImage hotelImage : roomImage) {
            // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
            if (imageMap.containsKey(hotelImage.getHotelImgUri())) {
                // 이미 존재하는 이미지를 유지
                updatedImages.add(imageMap.get(hotelImage.getHotelImgUri()));
            } else {
                // 새 이미지 추가
                updatedImages.add(hotelImage);
                hotelImage.setRoom(this);   // 관계 설정
            }
        }

        // 기존 리스트를 업데이트 된 리스트로 교체
        this.images.clear();
        this.images.addAll(updatedImages);

    }

}
