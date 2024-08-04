package com.yp.puppy.api.entity.hotel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yp.puppy.api.dto.request.hotel.ReservationSaveDto;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString(exclude = {"user", "room", "hotel"})
@EqualsAndHashCode(of = "reservationId")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
//    @GeneratedValue(generator = "uuid-generator")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "reservation_id")
    private String reservationId; // 예약 아이디 (UUID)

    @Column(nullable = false)
    private LocalDateTime reservationAt; // 예약 시간

    @Column(nullable = false)
    private LocalDateTime reservationEndAt; // 예약 종료 시간

    @Column(nullable = false, updatable = false)
    private LocalDateTime reservationCreateAt; // 예약 생성 시간

    @Column(nullable = false)
    private LocalDateTime reservationModifyAt;  // 예약 수정 시간

    @Setter
    @Column(nullable = false)
    private long price; // 예약 금액

    @Setter
    @Column(nullable = false)
    private CancellationStatus cancelled; // 예약 취소 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-reservations")
    private User user; // 예약한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference("room-reservation")
    private Room room; // 예약된 객실

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference("hotel-reservations")
    private Hotel hotel; // 예약된 호텔

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.reservationCreateAt = now;
        this.reservationModifyAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.reservationModifyAt = LocalDateTime.now();
    }

    public void changeReservation(ReservationSaveDto dto) {
        this.reservationAt = dto.getReservationAt();
        this.reservationEndAt = dto.getReservationEndAt();
        this.cancelled = dto.getCancelled();
    }
}
