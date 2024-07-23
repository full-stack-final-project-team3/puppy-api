package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.BundleCreateDto;
import com.yp.puppy.api.service.shop.BundleService;
import com.yp.puppy.api.service.shop.TreatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class BundleController {

    private final BundleService bundleService;

    // 1. 번들 생성
    @PostMapping("/bundle")
    public ResponseEntity<?> createBundle(TokenUserInfo userInfo,
                                          @RequestBody BundleCreateDto dto
    ) {
        try {
            bundleService.createBundle(dto, userInfo.getUserId());
            return ResponseEntity.ok().body("번들 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

//
//    private List<Treats> getTreatsFromDto(List<TreatsDto> treatsDtos, User user) {
//        return treatsDtos.stream()
//                .map(dto -> createTreatsFromDto(dto, user))
//                .collect(Collectors.toList());
//    }

}
