package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.LoginRequest;
import com.ceos23.spring_boot.dto.LoginResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponse>> login(
            @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}