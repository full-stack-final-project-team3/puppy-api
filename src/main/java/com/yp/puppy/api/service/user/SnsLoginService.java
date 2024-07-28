package com.yp.puppy.api.service.user;

import com.yp.puppy.api.dto.request.user.LoginRequestDto;
import com.yp.puppy.api.dto.request.user.UserSaveDto;
import com.yp.puppy.api.dto.response.user.AccessTokenDto;
import com.yp.puppy.api.dto.response.user.KakaoUserDto;
import com.yp.puppy.api.dto.response.user.LoginResponseDto;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.exception.LoginFailException;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SnsLoginService {

    private final UserService userService;
    private final UserRepository userRepository;

    // 카카오 로그인 처리 서비스 로직
    public void kakaoLogin(Map<String, Object> requestParams, HttpServletResponse response) {
        // 토큰 발급 요청
        String accessToken = getKakaoAccessToken(requestParams);

        // 발급받은 토큰으로 사용자 정보 가져오기
        KakaoUserDto userInfo = getKakaoUserInfo(accessToken);

        // 카카오에서 받은 회원정보로 우리사이트 회원가입 또는 로그인 처리
        KakaoUserDto.KakaoAccount kakaoAccount = userInfo.getKakaoAccount();
        String email = kakaoAccount.getEmail();
        KakaoUserDto.Properties properties = userInfo.getProperties();
        String nickname = properties.getNickname();
        String profileImage = properties.getProfileImage();

        // 사용자 존재 여부 확인
        boolean isUserExists = userService.checkIdentifier(email);
        User foundUser;
        if (!isUserExists) {
            // 사용자 정보로 회원가입 처리
            UserSaveDto kakaoUser = UserSaveDto.builder()
                    .email(email)
                    .nickname(nickname)
                    .password("abcd1234!") // 패스워드 설정 (안전한 방식으로 변경 고려)
                    .build();

            foundUser = userService.confirmSignUpKakao(kakaoUser);
            foundUser.setProfileUrl(profileImage);
            userRepository.save(foundUser);
        } else {
            foundUser = userRepository.findByEmail(email).orElseThrow(
                    () -> new RuntimeException("회원 정보가 존재하지 않습니다.")
            );
        }

        // 로그인 처리
        try {
            LoginRequestDto loginRequest = LoginRequestDto.builder()
                    .email(email)
                    .password("abcd1234!") // 패스워드 설정 (카카오 사용자는 패스워드가 필요 없을 수도 있음)
                    .autoLogin(true)
                    .build();

            LoginResponseDto loginResponse = userService.authenticate(loginRequest);

            // 자동 로그인 쿠키 설정
            Cookie cookie = new Cookie("authToken", loginResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30); // 쿠키 유효기간 30일
            response.addCookie(cookie);

        } catch (LoginFailException e) {
            throw new RuntimeException("카카오 로그인 실패", e);
        }
    }

    // 토큰으로 사용자 정보 요청
    private KakaoUserDto getKakaoUserInfo(String accessToken) {
        String requestUri = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 보내기
        RestTemplate template = new RestTemplate();
        ResponseEntity<KakaoUserDto> response = template.exchange(
                requestUri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                KakaoUserDto.class
        );

        // 응답 정보
        KakaoUserDto json = response.getBody();
        log.debug("user profile: {}", json);

        return json;
    }

    // 인가코드로 토큰 발급 요청
    private String getKakaoAccessToken(Map<String, Object> requestParams) {
        // 요청 URL
        String requestUri = "https://kauth.kakao.com/oauth/token";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", requestParams.get("appKey"));
        params.add("redirect_uri", requestParams.get("redirect"));
        params.add("code", requestParams.get("code"));

        // 요청 헤더와 바디를 담을 객체
        HttpEntity<Object> entity = new HttpEntity<>(params, headers);

        // 카카오 인증 서버로 POST 요청
        RestTemplate template = new RestTemplate();
        ResponseEntity<AccessTokenDto> response = template.exchange(requestUri, HttpMethod.POST, entity, AccessTokenDto.class);

        AccessTokenDto json = response.getBody();
        log.debug("access token response: {}", json);

        return json.getAccessToken();
    }
}