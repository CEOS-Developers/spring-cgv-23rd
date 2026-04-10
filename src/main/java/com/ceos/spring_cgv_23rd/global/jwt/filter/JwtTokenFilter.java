package com.ceos.spring_cgv_23rd.global.jwt.filter;

import com.ceos.spring_cgv_23rd.global.jwt.JwtTokenProvider;
import com.ceos.spring_cgv_23rd.global.jwt.enums.JwtErrorType;
import com.ceos.spring_cgv_23rd.global.jwt.enums.TokenType;
import com.ceos.spring_cgv_23rd.global.jwt.utils.CookieUtils;
import com.ceos.spring_cgv_23rd.global.security.AuthUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request);

        if (accessToken != null) {
            try {
                Claims claims = jwtTokenProvider.validateToken(accessToken);

                String tokenType = claims.get("tokenType", String.class);
                if (!TokenType.ACCESS_TOKEN.name().equals(tokenType)) {
                    throw new JwtException("토큰 타입이 일치하지 않습니다.");
                }

                Long userId = jwtTokenProvider.getUserIdFromClaims(claims);
                String role = jwtTokenProvider.getRoleFromClaims(claims);

                AuthUserDetails userDetails = new AuthUserDetails(userId, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", JwtErrorType.TOKEN_EXPIRED);
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute("exception", JwtErrorType.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(CookieUtils.ACCESS_TOKEN_COOKIE))
                    return cookie.getValue();
            }
        }

        // Swagger, Postman 등 테스트용
        if (!activeProfile.equals("prod")) {
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }


        return null;
    }
}
