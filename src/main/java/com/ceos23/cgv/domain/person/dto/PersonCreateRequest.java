package com.ceos23.cgv.domain.person.dto;

import com.ceos23.cgv.domain.person.enums.PersonType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PersonCreateRequest {
    private PersonType type;
    private String name;
    private String englishName;
    private LocalDate birthDate;
    private String award;
}