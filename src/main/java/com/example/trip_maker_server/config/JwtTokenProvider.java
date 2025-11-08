package com.example.trip_maker_server.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Base64; // (java.util.Base64 사용)

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        
        // application.properties의 secretKey를 HMAC-SHA 키로 변환
        // (주의: Base64 인코딩된 키가 아니므로 getBytes() 사용)
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = expirationMs;
    }

    /**
     * 사용자 정보로 Access Token을 생성합니다.
     */
    public String createToken(Integer userId, String nickname) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // (★) 토큰의 주체 (사용자 ID)
                .claim("nickname", nickname)     // (★) 닉네임 정보 (선택 사항)
                .issuedAt(now)                   // 발행 시간
                .expiration(validity)            // 만료 시간
                .signWith(key)                   // 서명 (HMAC-SHA 사용)
                .compact();
    }
    
    // (참고) 이하는 나중에 '인증'이 필요한 API(예: 마이페이지)에서 
    // 토큰을 검증할 때 사용됩니다.
    
    // public boolean validateToken(String token) { ... }
    // public Integer getUserIdFromToken(String token) { ... }
}