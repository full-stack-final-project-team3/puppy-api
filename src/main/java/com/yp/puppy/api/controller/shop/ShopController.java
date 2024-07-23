package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.service.shop.TreatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final TreatsService treatsService;

    // 1. 상품 전체 조회

    /**
     * @param sort   정렬 기준 (기본값 'name')
     * @param pageNo 페이지 번호 (기본값 1)
     * @return 간식 목록
     */
    @GetMapping("/treats")
    public ResponseEntity<?> list(@RequestParam(required = false, defaultValue = "name") String sort,
                                   TokenUserInfo userInfo,
                                  @RequestParam(defaultValue = "1") int pageNo) {

        Map<String, Object> treatsList = treatsService.getTreatsList(userInfo, pageNo, sort);
        if (treatsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(treatsList);

    }


    // 2. 제품 상세 조회
    /**
     * 호텔 상세 정보를 조회합니다.
     * @param treatsId URL 경로에서 제공된 제품의 식별자
     * @return 제품의 상세 정보를 반환합니다. 제품 ID가 유효하지 않으면, 400 Bad Request 를 반환.
     */
    @GetMapping("/{treatsId}")
    public ResponseEntity<?> getHotel(@PathVariable String treatsId) {
        try {
            TreatsDetialDto hotelDetail = hotelService.getHotelDetail(hotelId);
            return ResponseEntity.ok().body(hotelDetail);
        } catch (Exception e) {
            log.warn("호텔 ID로 조회하는 중 오류 발생 : {}", hotelId);
            return ResponseEntity.badRequest().body("잘못된 hotelId 입니다.");
        }

    }
}
