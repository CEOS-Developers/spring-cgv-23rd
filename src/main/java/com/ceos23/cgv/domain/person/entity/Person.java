package com.ceos23.cgv.domain.person.entity;

import com.ceos23.cgv.domain.person.enums.PersonType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "persons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Person {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PersonType type;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String englishName;

    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String award; //수상내역

    public static Person create(PersonType type, String name, String englishName,
                                LocalDate birthDate, String award) {
        return Person.builder()
                .type(type)
                .name(name)
                .englishName(englishName)
                .birthDate(birthDate)
                .award(award)
                .build();
    }
}
