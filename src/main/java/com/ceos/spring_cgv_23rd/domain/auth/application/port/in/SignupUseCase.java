package com.ceos.spring_cgv_23rd.domain.auth.application.port.in;

import com.ceos.spring_cgv_23rd.domain.auth.application.dto.command.SignupCommand;
import com.ceos.spring_cgv_23rd.domain.auth.application.dto.result.TokenResult;

public interface SignupUseCase {
    TokenResult signup(SignupCommand command);
}
