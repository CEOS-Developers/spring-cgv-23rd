package com.ceos23.cgv.domain.person.dto;

import com.ceos23.cgv.domain.person.entity.WorkParticipation;
import com.ceos23.cgv.domain.person.enums.PersonType;
import com.ceos23.cgv.domain.person.enums.RoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkParticipationResponse {
    private Long participationId;
    private Long personId;
    private String personName;
    private PersonType personType;
    private RoleType role;

    public static WorkParticipationResponse from(WorkParticipation participation) {
        return WorkParticipationResponse.builder()
                .participationId(participation.getId())
                .personId(participation.getPerson().getId())
                .personName(participation.getPerson().getName())
                .personType(participation.getPerson().getType())
                .role(participation.getRole())
                .build();
    }
}