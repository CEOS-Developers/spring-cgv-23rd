package com.ceos23.cgv.domain.person.dto;

import com.ceos23.cgv.domain.person.entity.WorkParticipation;
import com.ceos23.cgv.domain.person.enums.RoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonWorkResponse {
    private Long participationId;
    private Long movieId;
    private String movieTitle;
    private RoleType role;

    public static PersonWorkResponse from(WorkParticipation participation) {
        return PersonWorkResponse.builder()
                .participationId(participation.getId())
                .movieId(participation.getMovie().getId())
                .movieTitle(participation.getMovie().getTitle())
                .role(participation.getRole())
                .build();
    }
}