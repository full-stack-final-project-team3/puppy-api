package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.auth.TokenProvider.TokenUserInfo;
import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.service.hotel.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    /**
     * @param sort 정렬 기준 (기본값 'name')
     * @param pageNo 페이지 번호 (기본값 1)
     * @return 호텔 목록과 상태 코드
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false, defaultValue = "name") String sort,
                                  @RequestParam(defaultValue = "1") int pageNo) {

        Map<String, Object> hotels = hotelService.getHotels(pageNo, sort);
        if (hotels.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(hotels);

    }


    // 2. 호텔 상세 조회
    /**
     * 호텔 상세 정보를 조회합니다.
     * @param hotelId URL 경로에서 제공된 호텔의 식별자
     * @return 호텔의 상세 정보를 반환합니다. 호텔 ID가 유효하지 않으면, 400 Bad Request 를 반환.
     */
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotel(@PathVariable String hotelId) {
        try {
            HotelOneDto hotelDetail = hotelService.getHotelDetail(hotelId);
            return ResponseEntity.ok().body(hotelDetail);
        } catch (Exception e) {
            log.warn("호텔 ID로 조회하는 중 오류 발생 : {}", hotelId);
            return ResponseEntity.badRequest().body("잘못된 hotelId 입니다.");
        }

    }


    // 3 호텔 생성
    /**
     * @param userInfo 사용자 정보
     * @param dto 호텔 저장 데이터 전송 객체
     * @return 생성 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 작성가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> register(@AuthenticationPrincipal TokenUserInfo userInfo,
                                      @RequestBody HotelSaveDto dto) {
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
    /**
     * @param hotelId 삭제할 호텔의 아이디
     * @return 삭제 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 삭제가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotel(@PathVariable String hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return ResponseEntity.ok().body("삭제성공");
        } catch (Exception e) {
            log.warn("호텔 삭제에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("호텔을 찾지 못햇습니다..");
        }
    }


    // 5. 호텔 수정
    /**
     * @param dto 호텔 수정 정보를 담은 데이터 전송 객체
     * @param hotelId 수정할 호텔의 아이디
     * @return 수정 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 수정 가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{hotelId}")
    public ResponseEntity<?> modify(@RequestBody HotelSaveDto dto,
                                        @PathVariable String hotelId) {
        try {
            hotelService.updateHotel(dto, hotelId);
            return ResponseEntity.ok().body("수정 성공");
        } catch (Exception e) {
            log.warn("수정에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("수정할 호텔을 찾지 못했습니다..");
        }
    }


}