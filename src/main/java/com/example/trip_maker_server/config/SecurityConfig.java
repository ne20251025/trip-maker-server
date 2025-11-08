package com.example.trip_maker_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // (추가) 스프링 시큐리티를 활성화
public class SecurityConfig {

    // (기존) 비밀번호 암호화 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * (★핵심★)
     * Spring Security의 HTTP 보안 설정을 구성합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            // (1) CSRF(Cross-Site Request Forgery) 보호 비활성화
            // (REST API는 세션/쿠키 기반이 아니므로, CSRF 토큰이 필요 없음)
            .csrf(csrf -> csrf.disable())

            // (2) 세션 관리 정책 설정: STATELESS
            // (JWT 토큰 기반 인증이므로, 서버는 세션을 생성/사용하지 않음)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // (3) HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
                // (★핵심★)
                // "/api/users/signup"과 "/api/users/login" 경로는
                // '인증 없이(로그인 안 해도)' 접근을 허용 (permitAll)
                .requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                
                // (4) 그 외의 모든 요청(anyRequest)은
                // 반드시 '인증(로그인)'이 되어야만 접근 가능 (authenticated)
                .anyRequest().authenticated()
            );

        // (5) 폼 로그인, HTTP Basic 인증 비활성화 (우리는 JWT 토큰을 사용)
        http
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());


        return http.build();
    }
}