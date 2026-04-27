package com.ceos.spring_cgv_23rd.domain.auth.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.auth.adapter.in.web.dto.request.AuthRequest;
import com.ceos.spring_cgv_23rd.domain.auth.application.dto.command.LoginCommand;
import com.ceos.spring_cgv_23rd.domain.auth.application.dto.command.SignupCommand;
import org.springframework.stereotype.Component;

@Component
public class AuthRequestMapper {

    public SignupCommand toCommand(AuthRequest.SignupRequest request) {
        return new SignupCommand(
                request.username(),
                request.password(),
                request.name(),
                request.email(),
                request.phone(),
                request.birth(),
                request.nickname(),
                request.gender()
        );
    }

    public LoginCommand toCommand(AuthRequest.LoginRequest request) {
        return new LoginCommand(
                request.username(),
                request.password()
        );
    }
}
