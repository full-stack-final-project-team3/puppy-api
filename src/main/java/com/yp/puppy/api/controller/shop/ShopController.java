package com.yp.puppy.api.controller.shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    // 1. 상품 전체 조회

    /**
     * @param sort   정렬 기준 (기본값 'name')
     * @param pageNo 페이지 번호 (기본값 1)
     * @return 호텔 목록과 상태 코드
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false, defaultValue = "name") String sort,
                                  @RequestParam(defaultValue = "1") int pageNo) {

//        Map<String, Object> hotels = hotelService.getHotels(pageNo, sort);
//        if (hotels.isEmpty()){
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok().body(hotels);
        return null;
    }
}
