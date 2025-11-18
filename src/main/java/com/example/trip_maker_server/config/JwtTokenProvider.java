package com.example.trip_maker_server.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException; // (SecurityException 추가)
import lombok.extern.slf4j.Slf4j; // (로그 Import 추가)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User; // (Spring Security의 User)
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Slf4j // (로그 출력을 위해 @Slf4j 추가)
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = expirationMs;
    }

    /**
     * (기존) Access Token 생성
     */
    public String createToken(Integer userId, String nickname) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // (★) 토큰의 주체 (사용자 ID)
                .claim("nickname", nickname)     
                .issuedAt(now)                  
                .expiration(validity)           
                .signWith(key)                  
                .compact();
    }

    // --- (★신규★) 토큰 검증 및 정보 추출 로직 ---

    /**
     * (신규) 토큰을 받아 Claims(정보)를 추출
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key) // (v0.12+) 검증 키 설정
                    .build()
                    .parseSignedClaims(token) // (v0.12+)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // (v0.12+) getPayload() 대신 getClaims() 사용 가능
            return e.getClaims();
        }
    }

    /**
     * (신규) 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * (신규) 토큰에서 인증 정보(Authentication) 객체를 생성
     * (이 객체를 SecurityContext에 저장하면, Spring Security가 "인증됨"으로 간주)
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // (★) claims에서 'subject' (우리가 넣었던 userId)를 가져옴
        String userId = claims.getSubject();
        
        // (★) Spring Security의 UserDetails 객체를 생성
        // (DB에서 User를 다시 조회할 수도 있지만, 여기서는 토큰 정보만으로 생성)
        // (비밀번호는 모르므로 빈 문자열, 권한도 임시로 'USER' 부여)
        UserDetails userDetails = new User(userId, "", Collections.singletonList(() -> "ROLE_USER"));

        // (★) Spring Security가 사용하는 인증 토큰(Authentication) 생성
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * (신규) 토큰에서 사용자 ID(pk) 추출
     */
    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(parseClaims(token).getSubject());
    }
}