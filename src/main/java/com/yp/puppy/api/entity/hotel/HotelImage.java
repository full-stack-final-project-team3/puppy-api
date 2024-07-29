package com.yp.puppy.api.entity.hotel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@ToString(exclude = {"hotel", "room"})
@EqualsAndHashCode(of ="imageId")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hotel_images")
public class HotelImage {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "image_id")
    private String imageId; // 이미지 아이디

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType type; // 이미지 타입 (호텔이미지, 객실이미지)

    @Column(nullable = false)
    private String hotelImgUri; // 이미지 URI

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonBackReference("hotel-images")
    private Hotel hotel; // 호텔

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonBackReference("room-images")
    private Room room; // 객실
}
