package com.ceos23.cgv_clone.user.controller;

import com.ceos23.cgv_clone.global.jwt.CustomUserDetails;
import com.ceos23.cgv_clone.global.response.ApiResponse;
import com.ceos23.cgv_clone.global.response.SuccessCode;
import com.ceos23.cgv_clone.user.dto.reponse.LoginResponse;
import com.ceos23.cgv_clone.user.dto.request.LoginRequest;
import com.ceos23.cgv_clone.user.dto.request.ReissueRequest;
import com.ceos23.cgv_clone.user.dto.request.SignUpRequest;
import com.ceos23.cgv_clone.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, userService.login(request));
    }

    @PostMapping("/signup")
    public ApiResponse<LoginResponse> signUp(@RequestBody SignUpRequest request) {
        return ApiResponse.ok(SuccessCode.INSERT_SUCCESS, userService.signUp(request));
    }


    @PostMapping("/reissue")
    public ApiResponse<LoginResponse> reissue(@RequestBody ReissueRequest request) {
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, userService.reissue(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.logout(userDetails.getUserId());
        return ApiResponse.ok(SuccessCode.DELETE_SUCCESS);
    }
}
