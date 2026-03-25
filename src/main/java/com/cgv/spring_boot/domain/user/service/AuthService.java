package com.cgv.spring_boot.domain.user.service;

import com.cgv.spring_boot.domain.user.dto.request.LoginRequest;
import com.cgv.spring_boot.domain.user.dto.request.SignUpRequest;
import com.cgv.spring_boot.domain.user.dto.response.LoginResponse;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.global.common.code.ErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import com.cgv.spring_boot.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new BusinessException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
        }

        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        return userRepository.save(user).getId();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN);
        }

        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getLoginId());
        return new LoginResponse(accessToken);
    }
}
