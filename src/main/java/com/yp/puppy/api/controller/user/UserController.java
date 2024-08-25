package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.BoardResponseDto;
import com.yp.puppy.api.dto.request.user.LoginRequestDto;
import com.yp.puppy.api.dto.request.user.UserInfoModifyDto;
import com.yp.puppy.api.dto.request.user.UserSaveDto;
import com.yp.puppy.api.dto.response.user.LoginResponseDto;
import com.yp.puppy.api.dto.response.user.UserResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.exception.LoginFailException;
import com.yp.puppy.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/auth") // 0723 auth 제거
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;


    // 이메일 중복확인
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean flag = userService.checkEmailDuplicate(email);
        return ResponseEntity.ok().body(flag);
    }

    // 인증 코드 검증 API
    @GetMapping("/code")
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
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto dto, HttpServletResponse response, HttpSession session) {
        log.info("login request - {}", dto);

        try {
            LoginResponseDto loginResponse = userService.authenticate(dto);

            if (dto.isAutoLogin()) {
                log.info("dto's auto login - {}", dto.isAutoLogin());
                // 자동로그인 요청이면 토큰을 쿠키에 저장
                // 쿠키 생성
                Cookie cookie = new Cookie("authToken", loginResponse.getToken());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
                cookie.setSecure(false); // HTTPS가 아닌 경우 false로 설정

// 쿠키를 직접 헤더에 추가 (SameSite 속성 포함)
                response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; Secure=%s; SameSite=None",
                        cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getPath(), cookie.getSecure()));

                log.info("Adding cookie: {}={}; Max-Age={}; Path={}; HttpOnly={}; Secure={}",
                        cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getPath(), cookie.isHttpOnly(), cookie.getSecure());


            }

            return ResponseEntity.ok().body(loginResponse);

        } catch (LoginFailException e) {
            // 로그인을 실패한 상황
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }
    }


    // 유저 단일 조회
    @GetMapping("/{email}")
    public ResponseEntity<?> findUser(@PathVariable String email) {
        if (!email.contains("@")) return ResponseEntity.badRequest().body("카카오 로그인 시도");
        UserResponseDto foundUser = userService.findUserByEmail(email);
        log.info("found user by email : {}", foundUser);
        return ResponseEntity.ok().body(foundUser);
    }

    // 유저 정보 수정 (강아지도 조회해서 다시 넣어줘야함!)
    @PatchMapping("/{email}")
    public ResponseEntity<?> modify(@RequestBody UserInfoModifyDto dto,
                                    @PathVariable String email) {
        log.info("modify user info - {}", dto);
        try {
            userService.modifyUserInfo(dto, email);
            return ResponseEntity.ok().body("success");
        } catch (Exception e) {
            log.error("Error modifying user info", e);
            return ResponseEntity.badRequest().body("실패..");
        }
    }

    @GetMapping("/forgot-email")
    public ResponseEntity<?> forgotEmail(@RequestParam String email) {

        boolean flag = userService.existByEmail(email);

        if (flag) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }


    // 인증 코드 검증 API
    @GetMapping("/forgot-code")
    public ResponseEntity<?> forgotVerifyCode(@RequestParam String email, @RequestParam String code) {
        log.info("{}'s  verify code is [  {}  ]", email, code);
        boolean isMatch = userService.checkMatchCode(email, code);
        return ResponseEntity.ok().body(isMatch);
    }


    // 닉네임 중복확인
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(String nickname) {
        boolean flag = userService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok().body(flag);
    }

    // 휴대폰 번호 중복확인
    @GetMapping("/check-phoneNumber")
    public ResponseEntity<?> checkPhoneNumber(String phoneNumber) {
        boolean flag = userService.checkPhoneNumberDuplicate(phoneNumber);
        return ResponseEntity.ok().body(flag);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestParam String password, @RequestParam String email) {

        userService.changePassword(email, password);
        return ResponseEntity.ok().body("success");
    }


    @GetMapping("/check-password/{email}")
    public ResponseEntity<?> checkPassword(@RequestParam String password, @PathVariable String email) {
        log.info("파라미터로 받은 패스워드!! - {}", password);
        boolean flag = userService.isDuplicatePassword(password, email);
        if (flag) { // 일치하면
            log.info("true flag : {}", flag);
            return ResponseEntity.ok().body(true);
        } else {
            log.info("false flag : {}", flag);
            return ResponseEntity.badRequest().body(false);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        log.info("delete user : {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok().body("delete success");
    }

    // 회원가입 완료 후 자동로그인
    @PostMapping("/register-and-login")
    public ResponseEntity<?> registerAndLogin(@RequestBody LoginRequestDto dto) {
        log.info("Register and    login request - {}", dto);

        try {
            // 회원가입 처리
            UserSaveDto userSaveDto = new UserSaveDto(dto.getEmail(), dto.getPassword(), dto.getNickname(), dto.getAddress(), dto.getPhoneNumber(), dto.getDetailAddress());
//            log.info("dto 변환 - {}", userSaveDto);
            userService.confirmSignUp(userSaveDto);
//            log.info("confirmSignup - {}", userSaveDto);
            // 자동 로그인 처리
            LoginResponseDto loginResponse = userService.authenticate(dto);
//            log.info("자동 로그인 처리 완료");

            return ResponseEntity.ok().body(loginResponse);

        } catch (LoginFailException e) {
            // 로그인 실패 시
            String errorMessage = e.getMessage();
//            log.info("왜안됨?");
            return ResponseEntity.status(422).body(errorMessage);
        } catch (Exception e) {
            // 회원가입 실패 시
//            log.info("실패");
            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
        }
    }

    @GetMapping("/board/like/{userId}")
    public ResponseEntity<?> getMyLikeBoards(@PathVariable String userId) {
        List<BoardResponseDto> myBoardList = userService.getMyLikeBoardList(userId);
        return ResponseEntity.ok().body(myBoardList);
    }


    // 자동 로그인 요청 처리
    @PostMapping("/auto-login")
    public ResponseEntity<?> autoLogin(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("오토로그인 token : {}", bearerToken);

        if (bearerToken == null || bearerToken.isEmpty() || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        // Bearer 부분 제거
        String token = bearerToken.substring(7);
        log.info("오토로그인 메서드에서 추출한 token : {}", token);

        try {
            TokenProvider.TokenUserInfo tokenInfo = tokenProvider.validateAndGetTokenInfo(token);
            String userId = tokenInfo.getUserId();
            log.info("오토로그인 안에서의 유저아이디 : {}", userId);
            UserResponseDto userResponseDto = userService.findUserById(userId);

            if (userResponseDto == null) {
                return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
            }

            return ResponseEntity.ok().body(userResponseDto);

        } catch (Exception e) {
            log.error("자동 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(401).body("자동 로그인에 실패했습니다.");
        }
    }


    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logout(HttpServletResponse response, @PathVariable String userId) {
        // 쿠키 삭제
        userService.changeAutoLogin(userId);
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS에서만 전송되도록 할 경우
        cookie.setPath("/");
//        cookie.setDomain("localhost");
        cookie.setMaxAge(0); // 즉시 만료시키기
        response.addCookie(cookie);

        return ResponseEntity.ok().body("로그아웃되었습니다.");
    }
}
