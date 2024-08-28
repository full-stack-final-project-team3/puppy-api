package com.yp.puppy.api.config;

import com.yp.puppy.api.auth.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeRequests()
                // 인증된 사용자만 접근 가능
                .antMatchers(HttpMethod.POST,
                        "/api/reviews/**",
                        "/api/reservation/**",
                        "/board/**",
                        "/board/{boardId}/comments/**",
                        "/likes/**",
                        "/dog/register/**",
                        "/dog/allergy",
                        "/bundle/**",
                        "/cart/**",
                        "/shop/orders/**",
                        "/shop/orders/cancel/{orderId}",
                        "/notice/add/**",
                        "/notice/click/{noticeId}/{userId}",
                        "/notice/click/all/{userId}",
                        "/auto-login",
                        "/hotel/favorite",
                        "/hotel/upload").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.PATCH,
                        "/api/reviews/**",
                        "/api/reservation/**",
                        "/dog/{dogId}",
                        "/dog/allergy",
                        "/treats/{treatsId}",
                        "/{email}",
                        "/password").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT,
                        "/cart", "/board").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,
                        "/api/reviews/**",
                        "/api/reservation/**",
                        "/board/{id}",
                        "/board/{boardId}/comments/{replyId}",
                        "/board/{boardId}/comments/{replyId}/subReplies/{subReplyId}",
                        "/dog/{dogId}",
                        "/dog/allergy/**",
                        "/cart/{cartId}",
                        "/cart/bundle/{bundleId}",
                        "/board/{id}/deleteImage",
                        "/{userId}").hasAnyAuthority("USER", "ADMIN")
                // 관리자만 접근 가능한 경로

                .antMatchers(HttpMethod.POST,
                        "/hotel", "/treats", "/room").hasAuthority("ADMIN")  // 객실 생성도 관리자만 접근
                .antMatchers(HttpMethod.DELETE,
                        "/hotel/{hotelId}", "/treats/{treatsId}", "/room/{roomId}").hasAuthority("ADMIN")  // 객실 삭제도 관리자만 접근
                .antMatchers(HttpMethod.PATCH,
                        "/hotel/{hotelId}", "/treats/{treatsId}", "/room/{roomId}").hasAuthority("ADMIN")  // 객실 수정도 관리자만 접근
                // 리뷰 관련 보안 설정
                .antMatchers(HttpMethod.DELETE,
                        "/api/reviews/{reviewId}").access("hasAuthority('ADMIN') or @reviewSecurityService.isOwner(authentication, #reviewId)")
                .antMatchers(HttpMethod.PATCH,
                        "/api/reviews/{reviewId}").access("hasAuthority('ADMIN') or @reviewSecurityService.isOwner(authentication, #reviewId)")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()); // 미인증 사용자가 접근할 경우 403 응답

        // JWT 필터를 CORS 필터 후에 추가
        http.addFilterAfter(jwtAuthFilter, CorsFilter.class);

        return http.build();
    }
}
