package com.ceos.spring_cgv_23rd.domain.auth.controller;

import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthRequestDTO;
import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthResponseDTO;
import com.ceos.spring_cgv_23rd.domain.auth.service.AuthService;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import com.ceos.spring_cgv_23rd.global.jwt.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @Operation(summary = "게스트 로그인")
    @PostMapping("/guest")
    public ApiResponse<Void> issueGuestToken(HttpServletResponse response) {
        AuthResponseDTO.TokenResponseDTO tokens = authService.issueGuestToken();
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.createRefreshTokenCookie(tokens.accessToken()).toString());
        return ApiResponse.onSuccess("게스트 로그인 성공");
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<Void> signup(
            @RequestBody @Valid AuthRequestDTO.SignupRequestDTO request,
            HttpServletResponse response) {
        AuthResponseDTO.TokenResponseDTO tokens = authService.signup(request);
        addTokenCookies(response, tokens);
        return ApiResponse.onSuccess("회원가입 성공");
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ApiResponse<Void> login(
            @RequestBody @Valid AuthRequestDTO.LoginRequestDTO request,
            HttpServletResponse response) {
        AuthResponseDTO.TokenResponseDTO tokens = authService.login(request);
        addTokenCookies(response, tokens);
        return ApiResponse.onSuccess("로그인 성공");
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ApiResponse<Void> refresh(
            @Parameter(hidden = true)
            @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE) String refreshToken,
            HttpServletResponse response) {
        AuthResponseDTO.TokenResponseDTO tokens = authService.refresh(refreshToken);
        addTokenCookies(response, tokens);
        return ApiResponse.onSuccess("토큰 재발급 성공");
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @Parameter(hidden = true)
            @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        clearTokenCookies(response);
        return ApiResponse.onSuccess("로그아웃 성공");
    }


    private void addTokenCookies(HttpServletResponse response, AuthResponseDTO.TokenResponseDTO tokens) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.createAccessTokenCookie(tokens.accessToken()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.createRefreshTokenCookie(tokens.refreshToken()).toString());
    }

    private void clearTokenCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.deleteAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.deleteRefreshTokenCookie().toString());
    }
}
