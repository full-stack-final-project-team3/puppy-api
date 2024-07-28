package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.BundleCreateDto;
import com.yp.puppy.api.dto.request.shop.UpdateBundleDto;
import com.yp.puppy.api.service.shop.BundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

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

    // 3. 번들 구독 상태 변경
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PutMapping
    public ResponseEntity<?> checkOutCart(@AuthenticationPrincipal TokenUserInfo userInfo,
                                          UpdateBundleDto dto) {
        try {
            bundleService.updateCheckOutInfoCart(userInfo.getUserId(), dto);
            return ResponseEntity.ok().body("장바구니 상태 업데이트 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @DeleteMapping("/{bundleId}")
    public ResponseEntity<?> deleteBundleOnCart(@PathVariable String bundleId) {

        if (bundleId == null || bundleId.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않은 번들 ID입니다.");
        }

        try {
            bundleService.deleteBundle(bundleId);
            return ResponseEntity.ok().body("삭제 성공");
        } catch (EntityNotFoundException e) {
            log.warn("번들을 찾지 못했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("번들을 찾지 못했습니다.");
        } catch (Exception e) {
            log.error("번들 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }

    }

}
