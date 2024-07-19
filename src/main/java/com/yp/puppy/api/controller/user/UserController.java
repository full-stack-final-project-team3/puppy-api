package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.dto.request.user.LoginRequestDto;
import com.yp.puppy.api.dto.request.user.UserSaveDto;
import com.yp.puppy.api.dto.response.user.LoginResponseDto;
import com.yp.puppy.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final View error;


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

    // 회원가입 마무리 단계
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserSaveDto dto) {
        log.info("save User Info - {}", dto);
        try {
            // DB 저장 단계
            userService.confirmSignUp(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("saved success ^^");
    }

    // 로그인 로직
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto dto) {

        log.info("login request - {}", dto);

        try {
            LoginResponseDto response = userService.authenticate(dto);
            return ResponseEntity.ok().body(response);

        } catch (RuntimeException e) {
            // 로그인을 실패한 상황
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }
    }

}
