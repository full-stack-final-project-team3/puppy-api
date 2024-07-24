package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.UpdateCartDto;
import com.yp.puppy.api.entity.shop.Cart;
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

    // 3. 장바구니 상태 변경
    @PutMapping
    public ResponseEntity<?> checkOutCart(TokenUserInfo userInfo,
                                          UpdateCartDto dto) {
        try {
            cartService.updateCart(userInfo.getUserId(), dto);
            return ResponseEntity.ok().body("장바구니 수정 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

}
