package com.ceos23.cgv.domain.auth.controller;

import com.ceos23.cgv.domain.auth.dto.LoginRequest;
import com.ceos23.cgv.domain.auth.dto.SignupRequest;
import com.ceos23.cgv.domain.auth.dto.TokenResponse;
import com.ceos23.cgv.domain.auth.dto.UserResponse;
import com.ceos23.cgv.domain.auth.service.AuthService;
import com.ceos23.cgv.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. 회원가입 (BCryptPasswordEncoder로 비밀번호 암호화 후 저장)
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@RequestBody SignupRequest request) {
        UserResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    // 2. 로그인 (인증 후 Token 발급)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        // 클라이언트에게 토큰을 JSON 바디 또는 쿠키로 전달
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}
