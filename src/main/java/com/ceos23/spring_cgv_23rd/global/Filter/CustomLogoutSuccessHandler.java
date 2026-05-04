package com.ceos23.spring_cgv_23rd.global.Filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그아웃 요청에 성공한 경우 응답값을 반환합니다.
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @Nullable Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {
                    "message": "로그인 성공"
                }
        """);
    }
}
