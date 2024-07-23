package com.yp.puppy.api.dto.response.hotel;

import com.yp.puppy.api.entity.hotel.CancellationStatus;
import com.yp.puppy.api.entity.hotel.Reservation;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationOneDto {

    private String reservationId; // 예약 아이디 (UUID)
    private LocalDateTime reservationAt; // 예약 시간
    private LocalDateTime reservationEndAt; // 예약 종료 시간
    private long price; // 예약 금액
    private CancellationStatus cancelled; // 예약 취소 여부

    private String roomId; // 예약된 객실 ID
    private String userId; // 예약한 사용자 ID
    private String hotelId; // 예약된 호텔 ID

    public ReservationOneDto(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.reservationAt = LocalDateTime.now();
        this.reservationEndAt = LocalDateTime.now();
        this.price = reservation.getPrice();
        this.cancelled = reservation.getCancelled();
        this.roomId = reservation.getRoom().getRoomId();
        this.userId = reservation.getUser().getId();
        this.hotelId = reservation.getHotel().getHotelId();
    }
}
