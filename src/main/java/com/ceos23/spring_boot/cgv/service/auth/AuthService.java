package com.ceos23.spring_boot.cgv.service.auth;

import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.dto.auth.LoginRequest;
import com.ceos23.spring_boot.cgv.dto.auth.LoginResponse;
import com.ceos23.spring_boot.cgv.dto.auth.SignupRequest;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.global.security.TokenProvider;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public void signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException(ErrorCode.CONFLICT);
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserRole.USER
        );

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String accessToken = tokenProvider.createAccessToken(user.getId(), authentication);

        return new LoginResponse(accessToken);
    }
}