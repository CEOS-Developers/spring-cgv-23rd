package com.ceos23.cgv_clone.user.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.jwt.TokenProvider;
import com.ceos23.cgv_clone.global.jwt.domain.RefreshToken;
import com.ceos23.cgv_clone.global.jwt.repository.RefreshTokenRepository;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.user.entity.Role;
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
        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        // refreshToken 발급
        String refreshToken = issueAndSaveRefreshToken(user.getId());

        return LoginResponse.of(accessToken, refreshToken);
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
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = issueAndSaveRefreshToken(user.getId());

        return LoginResponse.of(accessToken, refreshToken);

    }

    @Transactional
    public LoginResponse reissue(ReissueRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        String newRefreshToken = issueAndSaveRefreshToken(refreshToken.getUserId());

        return LoginResponse.of(newAccessToken, newRefreshToken);

    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private String issueAndSaveRefreshToken(Long userId) {
        String refreshToken = tokenProvider.createRefreshToken(userId);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        rt -> rt.rotate(refreshToken, tokenProvider.getRefreshTokenExpiresAt()),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(userId)
                                .token(refreshToken)
                                .expiresAt(tokenProvider.getRefreshTokenExpiresAt())
                                .build())
                );

        return refreshToken;
    }
}
