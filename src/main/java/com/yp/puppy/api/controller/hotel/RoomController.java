package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.auth.TokenProvider.TokenUserInfo;
import com.yp.puppy.api.dto.request.hotel.RoomSaveDto;
import com.yp.puppy.api.dto.response.hotel.RoomOneDto;
import com.yp.puppy.api.service.hotel.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/room")
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    // 1. 객실 전체 조회
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRooms(@RequestParam String hotelId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reservationAt,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reservationEndAt) {
        try {
            List<RoomOneDto> availableRooms = roomService.getAvailableRooms(hotelId, reservationAt, reservationEndAt);
            return ResponseEntity.ok().body(availableRooms);
        } catch (Exception e) {
            log.warn("예약 가능한 객실 조회 중 오류 발생 : {}", hotelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약 가능한 객실 조회 중 오류가 발생했습니다.");
        }
    }

    // 2. 객실 생성
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addRoom(@AuthenticationPrincipal TokenUserInfo userInfo,
                                     @RequestBody RoomSaveDto dto) {
        try {
            roomService.saveRoom(dto, userInfo.getUserId());
            return ResponseEntity.ok().body("객실 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            // 401 권한이 안된다 임마
            return ResponseEntity.status(403).body(e.getMessage());
        }

    }

    // 3. 객실 단일 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        try {
            RoomOneDto roomDetail = roomService.getRoomDetail(roomId);
            return ResponseEntity.ok().body(roomDetail);
        } catch (Exception e) {
            log.warn("호텔 ID로 조회하는 중 오류 발생 : {}", roomId);
            return ResponseEntity.badRequest().body("잘못된 hotelId 입니다.");
        }
    }

    // 4. 객실 삭제
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity.ok().body("삭제성공");
        } catch (Exception e) {
            log.warn("객실 삭제에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("객실을 찾지 못햇습니다..");
        }
    }


    // 5. 객실 수정
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @RequestBody RoomSaveDto dto) {
        try {
            roomService.modifyRoom(roomId, dto);
            return ResponseEntity.ok().body("수정성공");
        } catch (Exception e) {
            log.warn("수정에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("수정할 호텔을 찾지 못했습니다..");
        }
    }


}
