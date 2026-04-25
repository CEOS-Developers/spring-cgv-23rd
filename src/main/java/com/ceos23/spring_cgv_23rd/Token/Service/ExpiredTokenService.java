package com.ceos23.spring_cgv_23rd.Token.Service;

import com.ceos23.spring_cgv_23rd.Token.DTO.ExpiredTokenResponseDTO;
import com.ceos23.spring_cgv_23rd.Token.Domain.RefreshToken;
import com.ceos23.spring_cgv_23rd.Token.Repository.TokenRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.CookieMaker;
import com.ceos23.spring_cgv_23rd.global.DTO.TokenResultDTO;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import com.ceos23.spring_cgv_23rd.global.JWTType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ExpiredTokenService {

    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final CookieMaker cookieMaker;

    @Transactional
    public ExpiredTokenResponseDTO rotateRefreshToken(String refreshToken,
                                                      HttpServletResponse res){
        RefreshToken token = validateAndGetToken(refreshToken);
        User user = token.getUser();

        try {
            token.use();
        } catch (ObjectOptimisticLockingFailureException oe){
            oe.printStackTrace();
            reUsedDetected(user);
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
        return ExpiredTokenResponseDTO.create(200, accessToken, newRefreshToken);
    }

    private void reUsedDetected(User user){
        tokenRepository.findUnexpiredTokenByUser(user).forEach(
                RefreshToken::revoke
        );
        //TODO:: LOGGING
        throw new CustomException(ErrorCode.REUSE_DETECTED);
    }

    private RefreshToken validateAndGetToken(String refreshToken){
        try {
            tokenProvider.validateToken(refreshToken);

            RefreshToken token = tokenRepository.findByToken(refreshToken).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_REFRESH_TOKEN)
            );

            User user = token.getUser();

            if (token.isRevoked()){
                // 로그아웃 등의 사유로 이미 폐기된 토큰
                throw new CustomException(ErrorCode.REVOKED_TOKEN);
            }

            if (token.isUsed()){
                //이미 사용된 토큰이 재사용 시도됨
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
