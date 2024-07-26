package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.BundleCreateDto;
import com.yp.puppy.api.service.shop.BundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/bundle")
@Slf4j
@RequiredArgsConstructor
public class BundleController {

    private final BundleService bundleService;

    // 1. 번들 생성
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/{dogId}")
    public ResponseEntity<?> createBundle(@AuthenticationPrincipal TokenUserInfo userInfo,
                                          @PathVariable String dogId,
                                          @RequestBody BundleCreateDto dto
    ) {
        // 개 아이디 받아와야함
        // 추후 BundleCreateDto가 제품 5개가 아니면 리턴
        try {
            bundleService.createBundle(userInfo.getEmail(), dogId, dto);
            return ResponseEntity.ok().body("번들 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

}
