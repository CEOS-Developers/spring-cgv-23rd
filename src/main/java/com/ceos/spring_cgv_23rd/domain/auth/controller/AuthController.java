package com.ceos.spring_cgv_23rd.domain.auth.controller;

import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthRequestDTO;
import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthResponseDTO;
import com.ceos.spring_cgv_23rd.domain.auth.service.AuthService;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import com.ceos.spring_cgv_23rd.global.jwt.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        addTokenCookies(response, tokens);
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


    private void addTokenCookies(HttpServletResponse response, AuthResponseDTO.TokenResponseDTO tokens) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.createAccessTokenCookie(tokens.accessToken()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.createRefreshTokenCookie(tokens.refreshToken()).toString());
    }
}
