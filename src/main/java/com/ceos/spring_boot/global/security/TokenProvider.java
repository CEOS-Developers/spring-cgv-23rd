package com.ceos.spring_boot.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private Key key;
    private final String secret;
    private final CustomUserDetailsService userDetailsService;
    private final long tokenValidityInMilliseconds;

    private static final String AUTHORITIES_KEY = "auth";

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            CustomUserDetailsService userDetailsService) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000; // 컴퓨터(자바)가 시간을 계산하는 기본 단위 = 밀리초
        this.userDetailsService = userDetailsService;
    }

    /**
     * 빈 연산이 끝난 후(의존성 주입 후) 주입받은 secret 값을 Base64 디코딩하여 HMAC-SHA 알고리즘에 사용할 Key 객체를 생성
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * HTTP 요청 헤더(Authorization)에서 Bearer 토큰 문자열을 추출
     */
    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값만 반환
        }
        return null;
    }

    /**
     * 유저 식별자(ID)와 권한 정보를 바탕으로 JWT 액세스 토큰을 생성
     */
    public String createAccessToken(Long id, Authentication authentication) {

        // 권한 정보를 하나의 문자열로 합침 (ex. "ROLE_USER,ROLE_ADMIN")
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(id.toString())       // 토큰의 주체 (유저의 PK ID)
                .claim(AUTHORITIES_KEY, authorities) // 커스텀 클레임으로 권한 정보 추가
                .signWith(key, SignatureAlgorithm.HS512) // 암호화 알고리즘 및 키 설정
                .setExpiration(validity)          // 만료 시간
                .compact();
    }

    /**
     * 토큰에서 유저의 식별자(Subject)를 추출
     */
    public String getTokenUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰의 정보를 바탕으로 DB에서 유저 정보를 조회하여 SecurityContext에 저장할 Authentication(인증) 객체를 생성
     */
    public Authentication getAuthentication(String token) {

        // 토큰 내부의 유저 ID로 UserDetailsService를 통해 정보를 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(getTokenUserId(token));

        // 인증 객체 반환
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 토큰이 위변조되지 않았는지, 만료되지 않았는지 검증
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
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
}