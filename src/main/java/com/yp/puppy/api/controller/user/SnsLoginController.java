package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.service.user.SnsLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SnsLoginController {

    private final SnsLoginService snsLoginService;

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
    public void kakaoCode(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        log.info("카카오 인가코드 발급 - {}", code);

        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("appKey", appKey);
        requestParams.put("redirect", redirectUrl);
        requestParams.put("code", code);

        try {
            snsLoginService.kakaoLogin(requestParams, response);
            response.sendRedirect("http://localhost:3000"); // 리액트 앱으로 리디렉트
        } catch (Exception e) {
            response.sendRedirect("http://localhost:3000/login/failure"); // 에러 발생 시 리디렉트
        }
    }
}