package com.ceos.spring_cgv_23rd.global.jwt.handler;

import com.ceos.spring_cgv_23rd.global.apiPayload.code.GeneralErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import com.ceos.spring_cgv_23rd.global.jwt.enums.JwtErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        JwtErrorType errorType = (JwtErrorType) request.getAttribute("exception");

        GeneralErrorCode errorCode = (errorType != null)
                ? errorType.getErrorCode()
                : GeneralErrorCode.MISSING_AUTH_INFO;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(errorCode, null)));
    }
}
