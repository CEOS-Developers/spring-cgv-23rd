package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomAuthenticationException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException ae) throws IOException, ServletException {
        if (ae instanceof CustomAuthenticationException cae){
            ErrDTO errDTO = ErrDTO.create(cae.getCode());
            String responseBody = objectMapper.writeValueAsString(errDTO);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(cae.getCode().getStatus().value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseBody);
        }
    }
}