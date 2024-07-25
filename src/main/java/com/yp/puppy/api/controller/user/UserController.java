package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.dto.request.user.LoginRequestDto;
import com.yp.puppy.api.dto.request.user.UserSaveDto;
import com.yp.puppy.api.dto.response.user.LoginResponseDto;
import com.yp.puppy.api.dto.response.user.UserResponseDto;
import com.yp.puppy.api.exception.LoginFailException;
import com.yp.puppy.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
//@RequestMapping("/auth") // 0723 auth 제거
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
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        log.info("login request - {}", dto);

        try {
            LoginResponseDto loginResponse = userService.authenticate(dto);

            if (dto.isAutoLogin()) {
                // 자동로그인 요청이면 토큰을 쿠키에 저장
                Cookie cookie = new Cookie("authToken", loginResponse.getToken());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 30); // 쿠키 유효기간 30일
                response.addCookie(cookie);
            }

            return ResponseEntity.ok().body(loginResponse);

        } catch (LoginFailException e) {
            // 로그인을 실패한 상황
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> findUser(@PathVariable String email) {
        log.info("find user by email : {}", email);
        UserResponseDto foundUser = userService.findByEmail(email);
        log.info("found user by email : {}", foundUser);
        return ResponseEntity.ok().body(foundUser);
    }

}
