package com.yp.puppy.api.controller;

import com.yp.puppy.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 이메일 중복확인
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean flag = userService.checkEmailDuplicate(email);
        return ResponseEntity.ok().body(flag);
    }

    // 인증 코드 검증 API
    @GetMapping("/code")            // 너 누구야,   코드는 뭐야
    public ResponseEntity<?> verifyCode(String email, String code) {
        log.info("{}'s verify code is [  {}  ]", email, code);
        boolean isMatch = userService.isMatchCode(email, code);
        return ResponseEntity.ok().body(isMatch);
    }
}
