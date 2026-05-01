package com.ceos23.cgv_clone.global.jwt;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.jwt.domain.RefreshToken;
import com.ceos23.cgv_clone.global.jwt.repository.RefreshTokenRepository;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public String issue(Long userId) {
        String token = tokenProvider.createRefreshToken(userId);
        LocalDateTime expiresAt = tokenProvider.getRefreshTokenExpiresAt();

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        rt -> rt.rotate(token, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(userId)
                                .token(token)
                                .expiresAt(expiresAt)
                                .build())
                );

        return token;
    }

    public RefreshToken getValidToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        return refreshToken;
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
