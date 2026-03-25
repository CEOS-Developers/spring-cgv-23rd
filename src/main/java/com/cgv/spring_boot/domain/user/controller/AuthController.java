package com.cgv.spring_boot.domain.user.controller;

import com.cgv.spring_boot.domain.user.dto.request.LoginRequest;
import com.cgv.spring_boot.domain.user.dto.request.SignUpRequest;
import com.cgv.spring_boot.domain.user.dto.response.LoginResponse;
import com.cgv.spring_boot.domain.user.service.AuthService;
import com.cgv.spring_boot.global.common.code.SuccessCode;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 이름을 받아 회원가입 진행")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@Valid @RequestBody SignUpRequest request) {
        Long userId = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.INSERT_SUCCESS, userId));
    }

    @Operation(summary = "로그인", description = "가입된 아이디, 비밀번호로 로그인 진행")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
