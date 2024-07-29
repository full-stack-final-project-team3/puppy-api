package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.UpdateBundleDto;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.service.shop.BundleService;
import com.yp.puppy.api.service.shop.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/cart")
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final BundleService bundleService;

    // 1. 유저가 생성한 번들을 포함한 장바구니 생성
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping
    public ResponseEntity<?> createCart(@AuthenticationPrincipal TokenUserInfo userInfo) {

        try {
            cartService.createUserCart(userInfo.getUserId());
            return ResponseEntity.ok().body("장바구니 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

    // 2. 유저의 장바구니 정보 조회
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal TokenUserInfo userInfo) {

        try {
            Cart cart = cartService.getCart(userInfo.getUserId());
//            if(cart.getBundles() == null) {
//                ResponseEntity.ok().body("장바구니가 비어있습니다.");
//            }
            return ResponseEntity.ok().body(cart);
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
            Cart cart = cartService.updateSubsInfoCart(userInfo.getUserId(), dto);
//            Cart cart = cartService.getCart(userInfo.getUserId());
            return ResponseEntity.ok().body(cart);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 4. 장바구니에서 번들 삭제
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/bundle/{bundleId}")
    public ResponseEntity<?> deleteBundleOnCart(@AuthenticationPrincipal TokenUserInfo userInfo,
                                                @PathVariable String bundleId) {

        String userId = userInfo.getUserId();

        if (bundleId == null || bundleId.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않은 번들 ID입니다.");
        }

        try {
            Cart cart = cartService.deleteBundle(userId, bundleId);
            return ResponseEntity.ok().body(cart);
        } catch (EntityNotFoundException e) {
            log.warn("번들을 찾지 못했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("번들을 찾지 못했습니다.");
        } catch (Exception e) {
            log.error("번들 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }

    }

    // 5. 장바구니 비우기
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable String cartId) {

        try {
            cartService.deleteCart(cartId);
            return ResponseEntity.ok().body("삭제 성공");
        } catch (EntityNotFoundException e) {
            log.warn("장바구니를 찾지 못했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("장바구니를 찾지 못했습니다.");
        } catch (Exception e) {
            log.error("장바구니 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }

    }

    // 6. 장바구니에서 번들의 구성 수정
    @PutMapping("/{bundleId}")
    public ResponseEntity<?> updateBundleInCart(@PathVariable String bundleId, @RequestBody UpdateBundleDto dto) {
        if (bundleId == null || bundleId.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않은 번들 ID입니다.");
        }

//        try {
//            cartService.updateBundleInCart(bundleId, dto);
//            return ResponseEntity.ok().body("번들 구성 수정 성공");
//        } catch (EntityNotFoundException e) {
//            log.warn("번들을 찾지 못했습니다.: {}", e.getMessage());
//            return ResponseEntity.status(404).body("번들을 찾지 못했습니다.");
//        } catch (Exception e) {
//            log.error("번들 수정 중 오류 발생: {}", e.getMessage());
//            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
//        }
        return null;
    }

}
