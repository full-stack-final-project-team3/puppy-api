package com.yp.puppy.api.entity.hotel;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@ToString(exclude = {"hotel", "room"})
@EqualsAndHashCode(of ="imageId")
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "hotel_images")
public class HotelImage {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
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
    private Hotel hotel; // 호텔

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room; // 객실
}
