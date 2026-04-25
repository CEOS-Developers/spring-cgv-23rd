package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
