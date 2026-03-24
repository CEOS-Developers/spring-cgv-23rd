package com.ceos23.cgv.domain.person.controller;

import com.ceos23.cgv.domain.person.dto.PersonCreateRequest;
import com.ceos23.cgv.domain.person.dto.PersonWorkResponse;
import com.ceos23.cgv.domain.person.dto.WorkParticipationRequest;
import com.ceos23.cgv.domain.person.dto.WorkParticipationResponse;
import com.ceos23.cgv.domain.person.entity.Person;
import com.ceos23.cgv.domain.person.entity.WorkParticipation;
import com.ceos23.cgv.domain.person.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Tag(name = "Person API", description = "영화 인물(배우/감독) 등록 및 참여작 관리 API")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    @Operation(summary = "인물 등록", description = "새로운 배우, 감독 등의 인물 정보를 DB에 등록합니다.")
    public ResponseEntity<Person> createPerson(@RequestBody PersonCreateRequest request) {
        Person person = personService.createPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(person);
    }

    @PostMapping("/participations")
    @Operation(summary = "영화 참여 정보 등록", description = "특정 인물을 특정 영화의 주연, 조연, 감독 등으로 연결합니다.")
    public ResponseEntity<WorkParticipationResponse> addParticipation(@RequestBody WorkParticipationRequest request) {
        WorkParticipation participation = personService.addWorkParticipation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WorkParticipationResponse.from(participation));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "특정 영화의 감독/출연진 조회", description = "영화 ID를 통해 해당 영화에 참여한 모든 인물(주연, 감독 등) 목록을 가져옵니다.")
    public ResponseEntity<List<WorkParticipationResponse>> getParticipantsByMovie(@PathVariable Long movieId) {
        List<WorkParticipationResponse> responses = personService.getParticipantsByMovieId(movieId).stream()
                .map(WorkParticipationResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{personId}/movies")
    @Operation(summary = "특정 인물의 필모그래피(출연작) 조회", description = "인물 ID를 통해 해당 배우/감독이 참여한 모든 영화 목록과 역할을 조회합니다.")
    public ResponseEntity<List<PersonWorkResponse>> getMoviesByPerson(@PathVariable Long personId) {
        List<PersonWorkResponse> responses = personService.getParticipationsByPersonId(personId).stream()
                .map(PersonWorkResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}