package com.yp.puppy.api.service.user;

import com.yp.puppy.api.dto.response.user.AccessTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Transactional
@Slf4j
public class SnsLoginService {



    // 카카오 로그인 처리 서비스 로직
    public void kakaoLogin(Map<String, Object> requestParams) {

        // 토큰 발급 요청
        String accessToken = getKakaoAccessToken(requestParams);

        // 발급받은 토큰으로 사용자 정보 가져오기
        getKakaoUserInfo(accessToken);

    }

    // 토큰으로 사용자 정보 요청
    private void getKakaoUserInfo(String accessToken) {

        String requestUri = "https://kapi.kakao.com/v2/user/me";

        // 2개 넣어야함
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 보내기
        RestTemplate template = new RestTemplate();
        ResponseEntity<Map> response = template.exchange(
                requestUri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Map.class
        );

        // 응답 정보 json
        Map json = response.getBody();
        log.debug("user profile: {}", json);
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
        params.add("redirect_url", requestParams.get("redirect"));
        params.add("code", requestParams.get("code"));

        // 요청헤더, 바디 담을 객체
        HttpEntity<Object> entity = new HttpEntity<>(params, headers);

        // 카카오 인증 서버로 post요청
        RestTemplate template = new RestTemplate();
        ResponseEntity<AccessTokenDto> response = template.exchange(requestUri, HttpMethod.POST, entity, AccessTokenDto.class);


        AccessTokenDto json = response.getBody();

//        log.debug("json: {}", json);
        String accessToken = json.getAccessToken();
        String refreshToken = json.getRefreshToken();

//        log.debug("accessToken - {}", accessToken);
//        log.debug("refreshToken - {}", refreshToken);

        return accessToken;
    }

}
