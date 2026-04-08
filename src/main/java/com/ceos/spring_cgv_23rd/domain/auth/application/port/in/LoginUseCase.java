package com.ceos.spring_cgv_23rd.domain.auth.application.port.in;

import com.ceos.spring_cgv_23rd.domain.auth.application.dto.command.LoginCommand;
import com.ceos.spring_cgv_23rd.domain.auth.application.dto.result.TokenResult;

public interface LoginUseCase {
    TokenResult login(LoginCommand command);
}
