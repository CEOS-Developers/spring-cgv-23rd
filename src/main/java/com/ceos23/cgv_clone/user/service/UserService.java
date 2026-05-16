package com.ceos23.cgv_clone.user.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.jwt.TokenProvider;
import com.ceos23.cgv_clone.global.jwt.domain.RefreshToken;
import com.ceos23.cgv_clone.global.jwt.RefreshTokenService;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.dto.response.LoginResponse;
import com.ceos23.cgv_clone.user.dto.request.LoginRequest;
import com.ceos23.cgv_clone.user.dto.request.ReissueRequest;
import com.ceos23.cgv_clone.user.dto.request.SignUpRequest;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_LOGGER");

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
					auditLog.warn("로그인 실패 email={} 사유=존재하지_않는_사용자", request.getEmail());
					return new CustomException(ErrorCode.USER_NOT_FOUND);
				});

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			auditLog.warn("로그인 실패 user_id={} email={} 사유=비밀번호_불일치", user.getId(), user.getEmail());
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // accessToken 발급
        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        // refreshToken 발급
        String refreshToken = refreshTokenService.issue(user.getId());

		auditLog.info("로그인 성공 user_id={}", user.getId());

        return LoginResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
			auditLog.warn("회원가입 실패 email={} 사유=중복_이메일", request.getEmail());
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.create(request.getEmail(), passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = refreshTokenService.issue(user.getId());

		auditLog.info("회원가입 성공 user_id={}", user.getId());

        return LoginResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse reissue(ReissueRequest request) {
        RefreshToken refreshToken;
        try {
            refreshToken = refreshTokenService.getValidToken(request.getRefreshToken());
        } catch (CustomException e) {
			auditLog.warn("토큰 재발급 실패 사유={}", e.getErrorCode());
            throw e;
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> {
					auditLog.warn("토큰 재발급 실패 user_id={} 사유=존재하지_않는_사용자", refreshToken.getUserId());
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        String newRefreshToken = refreshTokenService.issue(user.getId());

		auditLog.info("토큰 재발급 성공 user_id={}", user.getId());

        return LoginResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
		auditLog.info("로그아웃 성공 user_id={}", userId);
    }
}
