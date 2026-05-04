
package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomAuthenticationException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    public JWTAuthenticationFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            log.info("로그인 요청 도착. 정보없음");
            filterChain.doFilter(req, res);
            return;
        }

        String at = tokenProvider.getAccessToken(req);

        try {
            if (StringUtils.hasText(at)) {
                tokenProvider.validateToken(at);

                Authentication au = tokenProvider.getAuthentication(at);
                SecurityContextHolder.getContext().setAuthentication(au);

                UserDetails user = (UserDetails) au.getPrincipal();
                log.debug("로그인 검증 성공, userId={}", user.getUsername());
            }
        }
        catch (SignatureException sj){
            log.warn("시그니처가 위조된 요청 도착. token={}", at);
            throw new CustomAuthenticationException(ErrorCode.COUNTERFEIT_SIGNATURE);
        } catch (ExpiredJwtException ej){
            log.warn("만료된 토큰. token={}", at);
            throw new CustomAuthenticationException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException je){
            log.warn("기타 토큰에러. token={}", at);
            throw new CustomAuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e){
            log.error(String.valueOf(e.fillInStackTrace()));
            throw new CustomAuthenticationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        filterChain.doFilter(req, res);
    }

}