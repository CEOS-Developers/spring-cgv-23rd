package com.ceos23.spring_cgv_23rd.Token.Service;

import com.ceos23.spring_cgv_23rd.Token.DTO.ExpiredTokenResponseDTO;
import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.global.CookieMaker;
import com.ceos23.spring_cgv_23rd.global.DTO.TokenResultDTO;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpiredTokenService {

    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final CookieMaker cookieMaker;

    /**
     * 만료된 기존 토큰을 새 토큰으로 교체합니다.
     *
     * @param refreshToken 만료된 기존 리프레시 토큰
     * @param res HttpServletResponse
     * @return 교체된 토큰에 대한 정보
     */
    @Transactional
    public ExpiredTokenResponseDTO rotateRefreshToken(String refreshToken,
                                                      HttpServletResponse res){
        RefreshToken token = validateAndGetToken(refreshToken);
        User user = token.getUser();

        try {
            token.use();
        } catch (OptimisticLockException e) {
            log.warn("RTR 중 RaceCondition 발생: token={}, user={}", refreshToken, user.getLoginId());
            throw new CustomException(ErrorCode.CONFLICT);
        }

        //새 토큰들 발급
        //기본 토큰 무효화
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLoginId());
        String accessToken = tokenProvider.createToken(
                user.getLoginId(),
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ),
                JWTType.ACCESS
        );

        String newRefreshToken = tokenProvider.createToken(
                user.getLoginId(),
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ),
                JWTType.REFRESH
        );

        TokenResultDTO resultDTO = TokenResultDTO.create(
                accessToken, newRefreshToken
        );

        cookieMaker.setLoginCookie(res, resultDTO);
        tokenRepository.save(RefreshToken.create(user, newRefreshToken));
        log.info("새 토큰이 발급됨: 사용자={}", user.getLoginId());
        return ExpiredTokenResponseDTO.create(200, accessToken, newRefreshToken);
    }

    private void reUsedDetected(User user){
        tokenRepository.findUnexpiredTokenByUser(user).forEach(
                RefreshToken::revoke
        );
        throw new CustomException(ErrorCode.REUSE_DETECTED);
    }

    private RefreshToken validateAndGetToken(String refreshToken){
        try {

            RefreshToken token = tokenRepository.findByToken(refreshToken).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_REFRESH_TOKEN)
            );

            tokenProvider.validateToken(refreshToken);
            User user = token.getUser();

            if (token.isRevoked()){
                // 로그아웃 등의 사유로 이미 폐기된 토큰
                log.error("이미 폐기된 토큰을 재사용함. 토큰: {}, 사용자: {}", refreshToken, user.getLoginId());
                throw new CustomException(ErrorCode.REVOKED_TOKEN);
            }

            if (token.isUsed()){
                //이미 사용된 토큰이 재사용 시도됨
                log.error("이미 사용된 리프레시 토큰이 재사용됨. 토큰: {}, 사용자: {}", refreshToken, user.getLoginId());
                reUsedDetected(user);
            }

            return token;

        } catch (ExpiredJwtException eje){
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException je){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
