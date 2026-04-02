package com.ceos23.spring_boot.controller.auth.service;

import com.ceos23.spring_boot.controller.auth.dto.LoginRequest;
import com.ceos23.spring_boot.controller.auth.dto.SignupRequest;
import com.ceos23.spring_boot.controller.auth.dto.TokenResponse;
import com.ceos23.spring_boot.domain.user.entity.Role;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.security.jwt.TokenProvider;
import com.ceos23.spring_boot.global.security.refresh.RefreshToken;
import com.ceos23.spring_boot.global.security.refresh.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .email(request.email())
                .password(encodedPassword)
                .name(request.name())
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().name());

        String refreshToken = tokenProvider.createRefreshToken();
        refreshTokenRepository.save(new RefreshToken(
                refreshToken,
                user.getEmail(),
                tokenProvider.getRefreshTokenValiditySeconds()
        ));

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        RefreshToken existedRefreshToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        User user = userRepository.findByEmailAndDeletedAtIsNull(existedRefreshToken.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.delete(existedRefreshToken);

        String newAccessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String newRefreshToken = tokenProvider.createRefreshToken();

        refreshTokenRepository.save(new RefreshToken(
                newRefreshToken,
                user.getEmail(),
                tokenProvider.getRefreshTokenValiditySeconds()
        ));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findById(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}