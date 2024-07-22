package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.dto.request.hotel.ReservationSaveDto;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.service.hotel.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    // 호텔 id 룸 id 시작날짜 종료날짜 선택
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationSaveDto dto) {

        try {
            Reservation reservation = reservationService.createReservation(dto);
            return ResponseEntity.ok().body(reservation);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body(null);
        }

    }


    // 예약 조회



    // 예약 취소


}
