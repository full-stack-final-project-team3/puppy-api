package com.yp.puppy.api.service.user;

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



    }

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
        ResponseEntity<Map> response = template.exchange(requestUri, HttpMethod.POST, entity, Map.class);

        log.debug("response: {}", response);

        return null;
    }

}
