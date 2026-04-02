package com.ceos.spring_cgv_23rd.domain.auth.service;

import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthRequestDTO;
import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthResponseDTO;
import com.ceos.spring_cgv_23rd.domain.auth.entity.RefreshToken;
import com.ceos.spring_cgv_23rd.domain.auth.exception.AuthException;
import com.ceos.spring_cgv_23rd.domain.auth.repository.RefreshTokenRepository;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.domain.user.enums.UserRole;
import com.ceos.spring_cgv_23rd.domain.user.exception.UserErrorCode;
import com.ceos.spring_cgv_23rd.domain.user.repository.UserRepository;
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
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AuthResponseDTO.TokenResponseDTO issueGuestToken() {

        // accessToken 생성
        String accessToken = jwtTokenProvider.generateGuestAccessToken();

        return new AuthResponseDTO.TokenResponseDTO(accessToken, null);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO signup(AuthRequestDTO.SignupRequestDTO request) {

        // 만 14세 미만 검증
        if (Period.between(request.birth(), LocalDate.now()).getYears() < 14) {
            throw new GeneralException(AuthException.UNDER_AGE);
        }

        // 아이디 중복 검사
        if (userRepository.existsByUsername(request.username())) {
            throw new GeneralException(AuthException.DUPLICATE_LOGINID);
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new GeneralException(AuthException.DUPLICATE_EMAIL);
        }

        // 전화번호 중복 검사
        if (userRepository.existsByPhone(request.phone())) {
            throw new GeneralException(AuthException.DUPLICATE_PHONE);
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.nickname())) {
            throw new GeneralException(AuthException.DUPLICATE_NICKNAME);
        }


        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .birth(request.birth())
                .nickname(request.nickname())
                .gender(request.gender())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        return generateAndSaveTokens(user);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO login(AuthRequestDTO.LoginRequestDTO request) {

        // 유저 조회
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.INVALID_LOGIN));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(GeneralErrorCode.INVALID_LOGIN);
        }

        return generateAndSaveTokens(user);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO refresh(String refreshToken) {

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
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseGet(() -> {
                    // 해당 유저의 모든 refreshToken 삭제
                    refreshTokenRepository.deleteAllByUserId(userId);
                    log.warn("토큰 재사용 감지 - userId: {}", userId);
                    throw new GeneralException(GeneralErrorCode.INVALID_TOKEN);
                });

        // 기존 refreshToken 삭제
        refreshTokenRepository.delete(storedToken);

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));

        // 새 토큰 발급
        return generateAndSaveTokens(user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }


    private AuthResponseDTO.TokenResponseDTO generateAndSaveTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .expiryDate(jwtTokenProvider.getExpirationFromToken(refreshToken))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponseDTO.TokenResponseDTO(accessToken, refreshToken);

    }
}
