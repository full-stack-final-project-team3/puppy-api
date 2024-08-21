package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // 예약 중복확인
    List<Reservation> findByRoom_RoomIdAndReservationEndAtAfterAndReservationAtBefore(String roomId, LocalDateTime reservationAt, LocalDateTime reservationEndAt);

    // 예약된 객실 조회
    List<Reservation> findByUserId(String userId);

    // 일 / 주 / 월 지출 포인트 조회
    @Query("SELECT SUM(r.price) FROM Reservation r WHERE r.reservationCreateAt BETWEEN :start AND :end")
    Long sumTotalPriceByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
