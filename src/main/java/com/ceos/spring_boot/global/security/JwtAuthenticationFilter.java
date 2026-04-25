package com.ceos.spring_boot.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        // 아래 경로들은 필터를 거치지 않고 바로 통과
        return path.startsWith("/users/login") ||
                path.startsWith("/users/signup") ||
                path.startsWith("/swagger-ui") ||
                path.contains("swagger-ui") ||
                path.contains("api-docs") ||
                path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Request Header에서 JWT 토큰 추출
        String jwt = tokenProvider.getAccessToken(request);

        // 토큰 유효성 검사 (StringUtils.hasText로 null 및 빈문자열 체크)
        if (StringUtils.hasText(jwt) && tokenProvider.validateAccessToken(jwt)) {

            // 유효한 토큰이면 토큰으로부터 Authentication(인증 정보) 객체를 가져옴
            Authentication authentication = tokenProvider.getAuthentication(jwt);

            // SecurityContext에 인증 객체 저장 (-> 로그인 상태)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
                    authentication.getName(), request.getRequestURI());
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}