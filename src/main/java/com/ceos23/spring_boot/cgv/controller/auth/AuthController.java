package com.ceos23.spring_boot.cgv.controller.auth;

import com.ceos23.spring_boot.cgv.dto.auth.LoginRequest;
import com.ceos23.spring_boot.cgv.dto.auth.LoginResponse;
import com.ceos23.spring_boot.cgv.dto.auth.SignupRequest;
import com.ceos23.spring_boot.cgv.dto.auth.TokenRefreshRequest;
import com.ceos23.spring_boot.cgv.dto.auth.TokenRefreshResponse;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @RequestBody @Valid TokenRefreshRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authService.logout(userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}