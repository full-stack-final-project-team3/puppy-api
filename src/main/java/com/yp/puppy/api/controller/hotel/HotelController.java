package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.auth.TokenProvider.TokenUserInfo;
import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.service.hotel.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/hotel")
@Slf4j
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;


    // 1. 호텔 전체 조회
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String sort, @RequestParam(defaultValue = "1") int pageNo) throws InterruptedException {

        if (sort == null) {
            return ResponseEntity.badRequest().body("sort 파라미터가 없습니다.");
        }

        Map<String, Object> hotels = hotelService.getHotels(pageNo, sort);
        if (hotels.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(hotels);

    }


    // 2. 호텔 상세 조회
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotel(@PathVariable Long hotelId) {

        if (hotelId == null || hotelId < 1) {
            String errorMessage = "hotelId가 정확하지 않습니다.";
            log.warn(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }


        HotelOneDto hotelDetail = hotelService.getHotelDetail(hotelId);
        return ResponseEntity.ok().body(hotelDetail);
    }


    // 3 호텔 생성
    @PostMapping
    public ResponseEntity<?> register(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody HotelSaveDto dto) {
        try {
            hotelService.saveHotel(dto, userInfo.getUserId());
            return ResponseEntity.ok().body("호텔 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            // 401 권한이 안된다 임마
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }


    // 4. 호텔 삭제
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.ok().body("삭제성공");
    }


    // 5. 호텔 수정
    @PatchMapping("/{hotelId}")
    public ResponseEntity<?> modify(@RequestBody HotelSaveDto dto, @PathVariable Long hotelId) {
        hotelService.updateHotel(dto, hotelId);
        return ResponseEntity.ok().body("수정 성공");
    }


}
