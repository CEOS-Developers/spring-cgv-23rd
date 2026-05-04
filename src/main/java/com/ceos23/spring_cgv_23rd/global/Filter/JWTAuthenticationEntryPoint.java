package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 로그인 과정 중 AuthenticationException이 발생한 경우 처리합니다.
 */
@Slf4j
@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final TokenProvider tokenProvider;
    ObjectMapper objectMapper = new ObjectMapper();

    public JWTAuthenticationEntryPoint(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException ae) throws IOException, ServletException {
        if (ae instanceof CustomAuthenticationException cae){
            UserDetails userDetails = (UserDetails) tokenProvider.getAuthentication(tokenProvider.getAccessToken(request)).getDetails();
            String loginId = userDetails.getUsername();
            log.info("로그인 에러: userId={} | 에러코드={}", loginId, cae.getCode());

            ErrDTO errDTO = ErrDTO.create(cae.getCode());
            String responseBody = objectMapper.writeValueAsString(errDTO);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(cae.getCode().getStatus().value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseBody);
        }
    }
}