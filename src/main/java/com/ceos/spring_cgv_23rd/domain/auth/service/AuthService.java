package com.ceos.spring_cgv_23rd.domain.auth.service;

import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthRequestDTO;
import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO.TokenResponseDTO issueGuestToken();

    AuthResponseDTO.TokenResponseDTO signup(AuthRequestDTO.SignupRequestDTO request);

    AuthResponseDTO.TokenResponseDTO login(AuthRequestDTO.LoginRequestDTO request);
}
