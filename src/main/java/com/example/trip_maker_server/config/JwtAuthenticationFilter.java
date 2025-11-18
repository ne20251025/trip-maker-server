package com.example.trip_maker_server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * (★핵심★)
 * React가 보낸 'Authorization: Bearer <토큰>' 헤더를 가로채서
 * 토큰을 검증하고, 유효하다면 "인증 정보"를 SecurityContext에 저장하는 필터.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);

        // 2. (★) 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // (★) 토큰이 유효하면, 토큰으로부터 '인증 정보' 객체(Authentication)를 가져옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            
            // (★) SecurityContext에 이 '인증 정보'를 저장함
            // (이 시점부터 이 요청은 '인증된' 요청으로 간주됨)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터(FilterChain)로 요청을 넘김
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 'Bearer ' 토큰을 추출하는 헬퍼 메소드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 토큰 반환
        }
        return null;
    }
}