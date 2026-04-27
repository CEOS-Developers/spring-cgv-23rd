package com.ceos23.cgv_clone.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
