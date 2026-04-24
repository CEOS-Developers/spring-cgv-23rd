package com.ceos23.cgv.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Badge {
    NORMAL("일반"),
    VIP("VIP"),
    RVIP("RVIP"),
    VVIP("VVIP"),
    SVIP("SVIP");

    private final String description;
}
