package com.ceos23.spring_boot.controller.auth.controller;

import com.ceos23.spring_boot.controller.auth.dto.*;
import com.ceos23.spring_boot.controller.auth.service.AuthService;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.security.jwt.TokenProvider;
import com.ceos23.spring_boot.global.security.refresh.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "회원가입 및 로그인 인증 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenProvider tokenProvider;

    public static final String BEARER = "Bearer ";

    @Operation(summary = "회원가입", description = "사용자를 등록합니다.")
    @PostMapping("/api/auth/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);

        ResponseCookie cookie = CookieUtil.createCookie("refresh_token", tokens.refreshToken(), tokenProvider.getRefreshTokenValiditySeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(tokens.accessToken()));
    }

    @Operation(summary = "토큰 재발급", description = "쿠키의 Refresh Token을 이용해 새로운 토큰 쌍을 발급 받습니다.")
    @PostMapping("/api/auth/reissue")
    public ResponseEntity<ReissueResponse> reissue(
            @Parameter(hidden = true)
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.MISSING_REFRESH_TOKEN);
        }

        TokenResponse tokens = authService.reissue(refreshToken);

        ResponseCookie cookie = CookieUtil.createCookie("refresh_token", tokens.refreshToken(), tokenProvider.getRefreshTokenValiditySeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ReissueResponse(tokens.accessToken()));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하고 쿠키를 비웁니다.")
    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @Parameter(hidden = true)
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {

        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = null;

        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER))
            accessToken = bearer.substring(BEARER.length());

        authService.logout(accessToken, refreshToken);

        ResponseCookie deleteCookie = CookieUtil.emptyCookie("refresh_token");

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
