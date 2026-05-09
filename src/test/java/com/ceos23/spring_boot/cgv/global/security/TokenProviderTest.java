package com.ceos23.spring_boot.cgv.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("access and refresh tokens cannot be used interchangeably")
    void validateToken_rejectsWrongTokenType() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "1",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = tokenProvider.createAccessToken(1L, authentication);
        String refreshToken = tokenProvider.createRefreshToken(1L);

        assertThat(tokenProvider.validateAccessToken(accessToken)).isTrue();
        assertThat(tokenProvider.validateRefreshToken(accessToken)).isFalse();
        assertThat(tokenProvider.validateAccessToken(refreshToken)).isFalse();
        assertThat(tokenProvider.validateRefreshToken(refreshToken)).isTrue();
    }
}
