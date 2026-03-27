package com.ceos23.spring_boot.cgv.service.auth;

import com.ceos23.spring_boot.cgv.domain.auth.RefreshToken;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.dto.auth.LoginRequest;
import com.ceos23.spring_boot.cgv.dto.auth.LoginResponse;
import com.ceos23.spring_boot.cgv.dto.auth.SignupRequest;
import com.ceos23.spring_boot.cgv.dto.auth.TokenRefreshRequest;
import com.ceos23.spring_boot.cgv.dto.auth.TokenRefreshResponse;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.global.security.TokenProvider;
import com.ceos23.spring_boot.cgv.repository.auth.RefreshTokenRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.time.LocalDateTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        saveOrUpdateRefreshToken(user.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        Long userId = Long.parseLong(tokenProvider.getTokenUserId(refreshToken));

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST));

        if (!savedRefreshToken.getToken().equals(refreshToken)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        if (savedRefreshToken.isExpired()) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(user.getId());

        savedRefreshToken.update(
                newRefreshToken,
                LocalDateTime.now().plusSeconds(
                        tokenProvider.getRefreshTokenValidityInMilliseconds() / 1000
                )
        );

        return new TokenRefreshResponse(newAccessToken, newRefreshToken);
    }

    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                tokenProvider.getRefreshTokenValidityInMilliseconds() / 1000
        );

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        savedToken -> savedToken.update(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(
                                new RefreshToken(userId, refreshToken, expiresAt)
                        )
                );
    }
}