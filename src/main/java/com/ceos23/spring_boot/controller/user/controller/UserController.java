package com.ceos23.spring_boot.controller.user.controller;

import com.ceos23.spring_boot.controller.user.dto.userDto.UserResponse;
import com.ceos23.spring_boot.domain.user.dto.UserInfo;
import com.ceos23.spring_boot.domain.user.service.UserService;
import com.ceos23.spring_boot.global.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "JWT 토큰을 통해 내 정보를 조회합니다.")
    @GetMapping("/api/users/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        String email = customUserDetails.getEmail();

        UserInfo response = userService.getMyProfile(email);

        return ResponseEntity.ok(UserResponse.from(response));
    }
}
