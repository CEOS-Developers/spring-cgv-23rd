package com.ceos23.spring_cgv_23rd.Token.Service;

import com.ceos23.spring_cgv_23rd.global.JWTType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private Key key;

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    public TokenProvider(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("key >>> " + jwtSecretKey);
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token){
        getTokenLoginId(token);
    }
    
    public String getAccessToken(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if ("accessToken".equals(c.getName())) {
                return c.getValue();
            }
        }

        return null;
    }

    public String getRefreshToken(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null){
            return null;
        }

        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                return c.getValue();
            }
        }

        return null;
    }

    public String createToken(String loginId, Authentication authentication, JWTType type){
        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        int expiration = type.getValidTime() * 1000;

        return Jwts.builder()
                .subject(loginId)
                .expiration(new Date(new Date().getTime() + expiration))
                .claim("auth", authorities)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String getTokenLoginId(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getTokenLoginId(token));

        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
    }
}
