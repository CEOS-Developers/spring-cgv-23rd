package com.ceos.spring_boot.domain.user.controller;

import com.ceos.spring_boot.domain.user.dto.AuthRequest;
import com.ceos.spring_boot.domain.user.dto.AuthResponse;
import com.ceos.spring_boot.domain.user.dto.SignupRequest;
import com.ceos.spring_boot.domain.user.dto.SignupResponse;
import com.ceos.spring_boot.domain.user.service.AuthService;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Auth 관련 API", description = "인증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.INSERT_SUCCESS));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.GET_SUCCESS));
    }
}