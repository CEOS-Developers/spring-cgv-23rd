package com.ceos23.spring_cgv_23rd.global.Exception;

import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(@NonNull HttpServletRequest request,
                       HttpServletResponse response,
                       @NonNull AccessDeniedException ade) throws IOException, ServletException {

        ErrDTO errDTO = ErrDTO.create(ErrorCode.ACCESS_DENIED);
        String responseBody = objectMapper.writeValueAsString(errDTO);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
