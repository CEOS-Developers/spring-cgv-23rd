package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.LoginRequest;
import com.ceos23.spring_boot.dto.LoginResponse;
import com.ceos23.spring_boot.dto.SignUpRequest;
import com.ceos23.spring_boot.dto.SignUpResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<SignUpResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        SignUpResponse response = authService.signUp(request);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}