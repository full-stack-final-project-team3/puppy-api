package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // 예약 중복확인
    List<Reservation> findByRoom_RoomIdAndReservationEndAtAfterAndReservationAtBefore(String roomId, LocalDateTime reservationAt, LocalDateTime reservationEndAt);
}
