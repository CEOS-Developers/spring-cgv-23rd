package com.ceos.spring_cgv_23rd.domain.auth.application.dto.command;

public record LoginCommand(
        String username,
        String password
) {
}
