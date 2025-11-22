package com.example.trip_maker_server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// (★ 1. CORS 관련 import 추가)
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List; // (java.util.List 추가)

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
        .csrf(csrf -> csrf.disable())
            // (★ 2. CORS 설정 Bean을 SecurityFilterChain에 적용)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
            		// (A) '누구나' 접근 가능 (순서 중요!)
                    // 1. 정적 리소스 및 공통 인증 경로
                    .requestMatchers("/api/auth/**", "/uploads/**").permitAll() 
                    
                    // (★수정★) 2. 회원가입 및 로그인 경로 명시적 허용
                    // (기존 코드에서 이 부분이 빠져서 403 에러가 났습니다)
                    .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/users/check-id").permitAll() // (혹시 아이디 중복 체크가 있다면)

                    // 3. 스케줄/투어 조회 (비로그인 허용)
                    .requestMatchers(HttpMethod.GET, "/api/schedules").permitAll() 
                    .requestMatchers(HttpMethod.GET, "/api/schedules/*").permitAll() 
                    .requestMatchers(HttpMethod.GET, "/api/tours/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/tours/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/companions/**").permitAll()
                    .requestMatchers("/ws-stomp/**").permitAll()

                    // (B) '로그인한 사용자'만 접근 가능
                    .requestMatchers(HttpMethod.POST, "/api/schedules").authenticated() 
                    .requestMatchers(HttpMethod.GET, "/api/schedules/me").authenticated() 
                    .requestMatchers("/api/users/me", "/api/users/me/image").authenticated() 
                    .requestMatchers(HttpMethod.PUT, "/api/schedules/*").authenticated() 
                    .requestMatchers(HttpMethod.DELETE, "/api/schedules/*").authenticated()
                    
                    // (C) 나머지 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            )
            
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * (★ 3. CORS 설정 Bean 신규 생성)
     * React(localhost:3000)의 요청을 허용하기 위한 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // (★수정함) 기존: "http://localhost:3000" 만 허용
        // (★변경) 개발용 치트키: 모든 출처(*) 허용 패턴 사용
        // ngrok 주소가 매번 바뀌어도 신경 쓸 필요가 없어집니다.
        config.setAllowedOriginPatterns(List.of("*"));

        // 허용할 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 헤더
        config.setAllowedHeaders(List.of("*"));

        // 자격 증명 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}