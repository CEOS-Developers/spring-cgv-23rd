package com.ceos23.spring_boot.cgv.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider implements InitializingBean {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final UserDetailsService userDetailsService;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            UserDetailsService userDetailsService
    ) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long id, Authentication authentication) {
        long now = new Date().getTime();
        Date validity = new Date(now + accessTokenValidityInMilliseconds);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("auth", authorities)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createRefreshToken(Long id) {
        long now = new Date().getTime();
        Date validity = new Date(now + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getTokenUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(getTokenUserId(token));

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                token,
                userDetails.getAuthorities()
        );
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, ACCESS_TOKEN_TYPE);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, REFRESH_TOKEN_TYPE);
    }

    private boolean validateToken(String token, String expectedTokenType) {
        try {
            Claims claims = parseClaims(token);
            return expectedTokenType.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }
}
