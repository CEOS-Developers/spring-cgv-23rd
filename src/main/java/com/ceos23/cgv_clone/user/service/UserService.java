package com.ceos23.cgv_clone.user.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.jwt.TokenProvider;
import com.ceos23.cgv_clone.global.jwt.domain.RefreshToken;
import com.ceos23.cgv_clone.global.jwt.repository.RefreshTokenRepository;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.dto.response.LoginResponse;
import com.ceos23.cgv_clone.user.dto.request.LoginRequest;
import com.ceos23.cgv_clone.user.dto.request.ReissueRequest;
import com.ceos23.cgv_clone.user.dto.request.SignUpRequest;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // userId로 유저 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // accessToken 발급
        String token = tokenProvider.createAccessToken(user.getId());
        // refreshToken 발급
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        // 기존 토큰 있으면 rotate, 없으면 새로 저장
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        rt -> rt.rotate(refreshToken, tokenProvider.getRefreshTokenExpiresAt()),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(user.getId())
                                .token(refreshToken)
                                .expiresAt(tokenProvider.getRefreshTokenExpiresAt())
                                .build())
                );

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public LoginResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = tokenProvider.createAccessToken(user.getId());
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .expiresAt(tokenProvider.getRefreshTokenExpiresAt())
                .build());

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public LoginResponse reissue(ReissueRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String newAccessToken = tokenProvider.createAccessToken(refreshToken.getUserId());
        String newRefreshToken = tokenProvider.createRefreshToken(refreshToken.getUserId());

        refreshToken.rotate(newRefreshToken, tokenProvider.getRefreshTokenExpiresAt());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
