package com.ceos.spring_cgv_23rd.global.jwt;

import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.domain.user.enums.UserRole;
import com.ceos.spring_cgv_23rd.global.jwt.enums.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateGuestAccessToken() {
        return generateGuestToken(accessTokenExpiration, TokenType.ACCESS_TOKEN);
    }

    public String generateAccessToken(User user) {
        return generateToken(user.getId(), user.getRole(), accessTokenExpiration, TokenType.ACCESS_TOKEN);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user.getId(), user.getRole(), refreshTokenExpiration, TokenType.REFRESH_TOKEN);
    }

    private String generateToken(Long userId, UserRole role, Long expiresIn, TokenType tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiresIn);

        return Jwts.builder()
                .subject(userId.toString())
                .expiration(expiryDate)
                .claim("tokenType", tokenType.name())
                .claim("role", role.name())
                .issuedAt(now)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private String generateGuestToken(Long expiresIn, TokenType tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiresIn);

        return Jwts.builder()
                .expiration(expiryDate)
                .claim("tokenType", tokenType.name())
                .claim("role", UserRole.GUEST.name())
                .issuedAt(now)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims validateToken(String token) {
        try {
            return getClaims(token);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("JWT 서명 검증 실패: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있음: {}", e.getMessage());
            throw e;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            String extractedType = getClaims(token).get("tokenType").toString();

            return TokenType.ACCESS_TOKEN.name().equals(extractedType);
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            String extractedType = getClaims(token).get("tokenType").toString();

            return TokenType.REFRESH_TOKEN.name().equals(extractedType);
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isGuestToken(Claims claims) {
        return UserRole.GUEST.name().equals(claims.get("role"));
    }

    public Long getUserIdFromClaims(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromClaims(Claims claims) {
        return claims.get("role").toString();
    }

    public LocalDateTime getExpirationFromToken(String token) {
        return getClaims(token).getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
