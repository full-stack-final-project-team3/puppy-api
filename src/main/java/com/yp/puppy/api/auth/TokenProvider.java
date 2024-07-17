package com.yp.puppy.api.auth;

import com.yp.puppy.api.entity.Role;
import com.yp.puppy.api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    /**
     * JWT를 생성하는 메서드
     * @param user - 토큰에 포함될 로그인한 유저의 정보
     * @return - 생성된 JWT의 암호화된 문자열
     */
    public String createToken(User user) {

        /*
            토큰의 형태
            {
                "iss": "뽀로로월드",
                "exp": "2024-07-18",
                "iat": "2024-07-15",
                ...
                "email": "로그인한 사람 이메일",
                "role": "ADMIN"
                ...
                ===
                서명
            }
         */

        // 토큰에 들어갈 커스텀 데이터 (추가 클레임)
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().toString());

        return Jwts.builder()
                // token에 들어갈 서명
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes())
                        , SignatureAlgorithm.HS512
                )
                // payload에 들어갈 클레임 설정
                .setClaims(claims) // 추가 클레임은 항상 가장 먼저 설정
                .setIssuer("puppy-company") // 발급자 정보
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(Date.from(
                        Instant.now().plus(1, ChronoUnit.DAYS)
                )) // 토큰 만료 시간
                .setSubject(user.getId()) // 토큰을 식별할수 있는 유일한 값
                .compact();
    }

    /**
     * 클라이언트가 전송한 토큰을 디코딩(복호화)하여 토큰의 서명 위조 여부를 확인
     * 그리고 토큰을 JSON으로 파싱하여 안에 들어있는 클레임(토큰 정보)을 리턴
     *
     * @param token - 클라이언트가 보낸 토큰
     * @return - 토큰에 들어있는 인증 정보들을 리턴 - 회원 식별 ID, email, 권한
     */
    public TokenUserInfo validateAndGetTokenInfo(String token) {

        // parserBuilder는 해체할 때 쓰는 메서드
        Claims claims = Jwts.parserBuilder()
                // 토큰 발급자의 발급 당시 서명을 넣음
                .setSigningKey(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes())
                )
                // 서명위조 검사 진행 : 위조된 경우 Exception이 발생
                // 위조되지 않은 경우 클레임을 리턴
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.info("claims: {}", claims);

        // 토큰에 인증된 회원의 PK, email, 권한
        return TokenUserInfo.builder()
                .userId(claims.getSubject())
                .email(claims.get("email", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build()
                ;
    }



    // ======================================= //
    //  기존엔 유저의 PK만 리턴했지만,
    //  컨트롤러에게 많은 데이터를 보내기 위해 dto(객체) 생성

    @Getter
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenUserInfo {

        private String userId; // 기존 리턴값
        private String email;
        private Role role;
    }

}


