package com.ceos23.cgv.domain.person.dto;

import com.ceos23.cgv.domain.person.enums.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkParticipationRequest {
    private Long movieId;
    private Long personId;
    private RoleType role;
}