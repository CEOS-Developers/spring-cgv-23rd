package com.ceos.spring_cgv_23rd.domain.user.enums;

public enum UserRole {
    GUEST,
    USER,
    MANAGER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
