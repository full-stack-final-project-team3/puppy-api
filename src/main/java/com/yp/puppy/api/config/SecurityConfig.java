package com.yp.puppy.api.config;


import com.yp.puppy.api.auth.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@EnableWebSecurity
// ↓ 컨트롤러에서 사전, 사후에 권한정보를 캐치해서 막을건지. true는 open
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

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

        http.addFilterAfter(jwtAuthFilter, CorsFilter.class);

        return http.build();
    }
}
