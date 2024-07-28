package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.UpdateBundleDto;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.service.shop.BundleService;
import com.yp.puppy.api.service.shop.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/cart")
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final BundleService bundleService;

    // 1. 유저가 생성한 번들을 포함한 장바구니 생성
    @PostMapping
    public ResponseEntity<?> createCart(TokenUserInfo userInfo) {

        try {
            cartService.createUserCart(userInfo.getUserId());
            return ResponseEntity.ok().body("장바구니 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

    // 2. 유저의 장바구니 정보 조회
    @GetMapping
    public ResponseEntity<?> getCart(TokenUserInfo userInfo) {

        try {
            Cart cart = cartService.getCart(userInfo.getUserId());
            return ResponseEntity.ok().body(cart);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

    // 3. 번들 구독 상태 변경
    @PutMapping
    public ResponseEntity<?> checkOutCart(TokenUserInfo userInfo,
                                          UpdateBundleDto dto) {
        try {
            cartService.updateCheckOutInfoCart(userInfo.getUserId(), dto);
            return ResponseEntity.ok().body("장바구니 상태 업데이트 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 4. 장바구니에서 번들 삭제
    @DeleteMapping("/{bundleId}")
    public ResponseEntity<?> deleteBundleOnCart(@PathVariable String bundleId) {
        try {
            cartService.deleteBundle(bundleId);
            return ResponseEntity.ok().body("삭제성공");
        } catch (Exception e) {
            log.warn("번들 삭제에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("번들을 찾지 못햇습니다..");
        }
    }

    // 5. 장바구니에서 번들의 구성 수정

}
