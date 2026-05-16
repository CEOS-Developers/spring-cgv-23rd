package com.cgv.spring_boot.domain.user.service;

import com.cgv.spring_boot.domain.user.dto.request.LoginRequest;
import com.cgv.spring_boot.domain.user.dto.request.SignUpRequest;
import com.cgv.spring_boot.domain.user.dto.response.LoginResponse;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.entity.UserRole;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import com.cgv.spring_boot.global.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long signup(SignUpRequest request) {
        if (userRepository.findByLoginId(request.loginId()).isPresent()) {
            log.warn("signup rejected. loginId={}", request.loginId());
            throw new BusinessException(UserErrorCode.LOGIN_ID_ALREADY_EXISTS);
        }

        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(UserRole.USER)
                .build();

        Long userId = userRepository.save(user).getId();
        log.info("user signed up. userId={}, loginId={}", userId, request.loginId());
        return userId;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> {
                    log.warn("AUDIT login failed. loginId={}, reason=invalid_login_id", request.loginId());
                    return new BusinessException(UserErrorCode.INVALID_LOGIN);
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("AUDIT login failed. loginId={}, userId={}, reason=invalid_password", request.loginId(), user.getId());
            throw new BusinessException(UserErrorCode.INVALID_LOGIN);
        }

        String accessToken = jwtTokenProvider.generateToken(user.getId());
        log.info("AUDIT login success. userId={}, loginId={}", user.getId(), request.loginId());
        return new LoginResponse(accessToken);
    }
}
