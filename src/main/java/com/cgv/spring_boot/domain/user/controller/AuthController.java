package com.cgv.spring_boot.domain.user.controller;

import com.cgv.spring_boot.domain.user.dto.request.LoginRequest;
import com.cgv.spring_boot.domain.user.dto.request.SignUpRequest;
import com.cgv.spring_boot.domain.user.dto.response.LoginResponse;
import com.cgv.spring_boot.domain.user.service.AuthService;
import com.cgv.spring_boot.global.common.code.SuccessCode;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@Valid @RequestBody SignUpRequest request) {
        Long userId = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.INSERT_SUCCESS, userId));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
