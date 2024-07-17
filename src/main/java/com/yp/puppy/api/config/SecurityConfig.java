package com.yp.puppy.api.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 설정 (스프링 부트 2.7버전 이전 인터페이스를 통해 오버라이딩)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf().disable() // 필터설정 off
                .httpBasic().disable() // 베이직 인증 off
                .formLogin().disable() // 로그인창 off
                // 여기까지는 시큐리티에서 기본제공하는 기능 다 off
                .authorizeRequests() // 요청 별로 인가 설정
                .antMatchers("/**").permitAll() // 인가 설정 off
        ;
        return http.build();
    }
}
