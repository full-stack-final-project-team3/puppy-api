package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.dto.request.hotel.ReservationSaveDto;
import com.yp.puppy.api.dto.response.hotel.ReservationOneDto;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.service.hotel.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    // 호텔 id 룸 id 시작날짜 종료날짜 선택
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReservation(@RequestBody ReservationSaveDto dto) {
        try {
            Reservation reservation = reservationService.createReservation(dto);
            return ResponseEntity.ok().body(reservation);
        } catch (IllegalArgumentException e) {
            log.warn("예약 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }


    // 예약 조회
    @GetMapping("/{reservationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReservation(@PathVariable String reservationId) {
        try {
            ReservationOneDto detailReservation = reservationService.getDetailReservation(reservationId);
            return ResponseEntity.ok().body(detailReservation);
        } catch (Exception e) {
            log.warn("예약 ID로 조회하는 중 오류 발생 : {}", reservationId);
            return ResponseEntity.badRequest().body("잘못된 reservationId 입니다.");
        }

    }


    // 내가 작성한 예약 조회
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReservationsByUserId(@PathVariable String userId) {
        try {
            log.info("API 호출: 사용자 {}의 예약 조회", userId);
            List<ReservationOneDto> reservations = reservationService.getReservationsByUserId(userId);
            log.info("API 응답: {}", reservations);
            return ResponseEntity.ok().body(reservations);
        } catch (Exception e) {
            log.warn("사용자 ID로 예약 조회 중 오류 발생 : {}", userId);
            return ResponseEntity.badRequest().body("잘못된 사용자 ID입니다.");
        }
    }


    // 예약 취소
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReservation(@PathVariable String reservationId) {
        try {
            reservationService.deleteReservation(reservationId);
            return ResponseEntity.ok().body("삭제 성공");
        } catch (Exception e) {
            log.warn("예약 삭제에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("예약을 찾지 못햇습니다..");
        }
    }


    // 예약 수정
    @PatchMapping("/{reservationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReservation(@PathVariable String reservationId, @RequestBody ReservationSaveDto dto) {
        try {
            reservationService.modify(reservationId, dto);
            return ResponseEntity.ok().body("수정 성공");
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }



}
