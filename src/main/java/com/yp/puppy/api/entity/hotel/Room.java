package com.yp.puppy.api.entity.hotel;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // 객실이 속한 호텔

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Dog> dogs = new ArrayList<>(); // 애완견 목록

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reservation> reservations =  new ArrayList<>(); // 객실의 예약 목록
}
