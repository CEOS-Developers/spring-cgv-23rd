package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * 로그아웃 요청 시 쿠키 제거 및 토큰 무효화 등의 기능을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @Nullable Authentication authentication) {
        String token = tokenProvider.getRefreshToken(request);

        if(token != null){
            tokenRepository.findByToken(token)
                    .ifPresent(RefreshToken::revoke);
        }

        UserDetails au = (UserDetails) tokenProvider.getAuthentication(token).getDetails();
        String userLoginId = au.getUsername();

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        Cookie cookie2 = new Cookie("accessToken", null);
        cookie2.setMaxAge(0);
        cookie2.setPath("/");
        response.addCookie(cookie2);

        log.debug("logout: userId={}", userLoginId);
    }
}
