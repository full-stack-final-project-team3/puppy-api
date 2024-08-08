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
                .antMatchers("/mypage").authenticated() // /mypage 경로에 대한 인증 필요
                // 리뷰 및 예약 생성, 수정, 삭제는 인증된 사용자만
                .antMatchers(HttpMethod.POST, "/api/reviews/**", "/api/reservation/**").authenticated()
                .antMatchers(HttpMethod.PATCH, "/api/reviews/**", "/api/reservation/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/reviews/**", "/api/reservation/**").authenticated()
                .antMatchers("/**").permitAll() // 나머지 경로에 대한 접근 허용
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()); // 미인증 사용자가 접근할 경우 403 응답

        http.addFilterAfter(jwtAuthFilter, CorsFilter.class);

        return http.build();
    }
}