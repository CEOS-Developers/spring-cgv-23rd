package com.ceos23.cgv.domain.person.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    LEAD_ACTOR("주연"),
    SUPPORTING_ACTOR("조연"),
    CAMEO("카메오"),
    DIRECTOR("감독");

    private final String description;
}
