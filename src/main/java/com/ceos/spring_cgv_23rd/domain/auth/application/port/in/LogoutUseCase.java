package com.ceos.spring_cgv_23rd.domain.auth.application.port.in;

public interface LogoutUseCase {
    void logout(String refreshToken);
}
