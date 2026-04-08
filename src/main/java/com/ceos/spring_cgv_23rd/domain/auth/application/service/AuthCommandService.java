package com.ceos.spring_cgv_23rd.domain.auth.application.service;

import com.ceos.spring_cgv_23rd.domain.auth.application.dto.command.LoginCommand;
import com.ceos.spring_cgv_23rd.domain.auth.application.dto.command.SignupCommand;
import com.ceos.spring_cgv_23rd.domain.auth.application.dto.result.TokenResult;
import com.ceos.spring_cgv_23rd.domain.auth.application.port.in.*;
import com.ceos.spring_cgv_23rd.domain.auth.application.port.out.RefreshTokenPersistencePort;
import com.ceos.spring_cgv_23rd.domain.auth.domain.RefreshToken;
import com.ceos.spring_cgv_23rd.domain.auth.exception.AuthException;
import com.ceos.spring_cgv_23rd.domain.user.application.port.out.UserPersistencePort;
import com.ceos.spring_cgv_23rd.domain.user.domain.User;
import com.ceos.spring_cgv_23rd.domain.user.domain.UserRole;
import com.ceos.spring_cgv_23rd.domain.user.exception.UserErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.code.GeneralErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import com.ceos.spring_cgv_23rd.global.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCommandService implements SignupUseCase, LoginUseCase, IssueGuestTokenUseCase, RefreshTokenUseCase, LogoutUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserPersistencePort userPersistencePort;
    private final RefreshTokenPersistencePort refreshTokenPersistencePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResult issueGuestToken() {
        // accessToken 생성
        String accessToken = jwtTokenProvider.generateGuestAccessToken();

        return new TokenResult(accessToken, null);
    }

    @Override
    @Transactional
    public TokenResult signup(SignupCommand command) {

        // 만 14세 미만 검증
        if (Period.between(command.birth(), LocalDate.now()).getYears() < 14) {
            throw new GeneralException(AuthException.UNDER_AGE);
        }

        // 중복 검사
        if (userPersistencePort.existsByUsername(command.username())) {
            throw new GeneralException(AuthException.DUPLICATE_LOGINID);
        }
        if (userPersistencePort.existsByEmail(command.email())) {
            throw new GeneralException(AuthException.DUPLICATE_EMAIL);
        }
        if (userPersistencePort.existsByPhone(command.phone())) {
            throw new GeneralException(AuthException.DUPLICATE_PHONE);
        }
        if (userPersistencePort.existsByNickname(command.nickname())) {
            throw new GeneralException(AuthException.DUPLICATE_NICKNAME);
        }


        User user = User.builder()
                .username(command.username())
                .password(passwordEncoder.encode(command.password()))
                .name(command.name())
                .email(command.email())
                .phone(command.phone())
                .birth(command.birth())
                .nickname(command.nickname())
                .gender(command.gender())
                .role(UserRole.USER)
                .build();

        User savedUser = userPersistencePort.save(user);

        return generateAndSaveTokens(savedUser.getId(), savedUser.getRole());
    }

    @Override
    @Transactional
    public TokenResult login(LoginCommand command) {

        // 유저 조회
        User user = userPersistencePort.findByUsername(command.username())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.INVALID_LOGIN));

        // 비밀번호 검증
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new GeneralException(GeneralErrorCode.INVALID_LOGIN);
        }

        return generateAndSaveTokens(user.getId(), user.getRole());
    }

    @Override
    @Transactional
    public TokenResult refresh(String refreshToken) {

        // refreshToken 유효성 검증
        Claims claims;
        try {
            claims = jwtTokenProvider.validateToken(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new GeneralException(GeneralErrorCode.INVALID_TOKEN);
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new GeneralException(GeneralErrorCode.INVALID_TOKEN);
        }

        // 게스트 토큰은 갱신 불가
        if (jwtTokenProvider.isGuestToken(claims)) {
            throw new GeneralException(GeneralErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserIdFromClaims(claims);

        // DB에 존재하는지 확인
        RefreshToken storedToken = refreshTokenPersistencePort.findByToken(refreshToken)
                .orElseGet(() -> {
                    // 해당 유저의 모든 refreshToken 삭제
                    refreshTokenPersistencePort.deleteAllByUserId(userId);
                    log.warn("토큰 재사용 감지 - userId: {}", userId);
                    throw new GeneralException(GeneralErrorCode.INVALID_TOKEN);
                });

        // 기존 refreshToken 삭제
        refreshTokenPersistencePort.delete(storedToken);

        // 유저 조회
        User user = userPersistencePort.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));

        // 새 토큰 발급
        return generateAndSaveTokens(user.getId(), user.getRole());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenPersistencePort.deleteByToken(refreshToken);
    }


    private TokenResult generateAndSaveTokens(Long userId, UserRole role) {
        String accessToken = jwtTokenProvider.generateAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, role);

        RefreshToken refreshTokenDomain = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expiryDate(jwtTokenProvider.getExpirationFromToken(refreshToken))
                .build();

        refreshTokenPersistencePort.save(refreshTokenDomain);

        return new TokenResult(accessToken, refreshToken);
    }
}
