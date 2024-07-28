package com.yp.puppy.api.controller.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Slf4j
public class SnsLoginController {

    @Value("${sns.kakao.app-key}")
    private String appKey;

    @Value("${sns.kakao.redirect-url}")
    private String redirectUrl;

    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin() {

        String uri = "https://kauth.kakao.com/oauth/authorize";
        uri += "?client_id=" + appKey;
        uri += "&redirect_uri=" + redirectUrl;
        uri += "&response_type=code";

        return ResponseEntity.status(302).header("Location", uri).build();
    }

    // 인가코드를 받는 요청 메서드
    @GetMapping("/oauth/kakao")
    public ResponseEntity<?> kakaoCode(String code) {
        log.info("카카오 인가코드 발급 - {}", code);
        log.info("카카오 인가코드 발급 - {}", code);

        // 토큰 발급에 필요한 파라미터 만들기
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("appKey", appKey);
        requestParams.put("redirect", redirectUrl);
        requestParams.put("code", code);

        // 인증 액세스 토큰 발급 요청
        return null;
    }

}