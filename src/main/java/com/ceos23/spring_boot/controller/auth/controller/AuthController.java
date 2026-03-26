package com.ceos23.spring_boot.controller.auth.controller;

import com.ceos23.spring_boot.controller.auth.dto.LoginRequest;
import com.ceos23.spring_boot.controller.auth.dto.SignupRequest;
import com.ceos23.spring_boot.controller.auth.dto.TokenResponse;
import com.ceos23.spring_boot.controller.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "7. Auth", description = "회원가입 및 로그인 인증 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "사용자를 등록합니다.")
    @PostMapping("/api/auth/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/api/auth/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);

        return ResponseEntity.ok(tokenResponse);
    }


}
