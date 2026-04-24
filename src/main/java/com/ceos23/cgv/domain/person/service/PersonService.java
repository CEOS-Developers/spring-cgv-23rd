package com.ceos23.cgv.domain.person.service;

import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.person.dto.PersonCreateRequest;
import com.ceos23.cgv.domain.person.dto.WorkParticipationRequest;
import com.ceos23.cgv.domain.person.entity.Person;
import com.ceos23.cgv.domain.person.entity.WorkParticipation;
import com.ceos23.cgv.domain.person.repository.PersonRepository;
import com.ceos23.cgv.domain.person.repository.WorkParticipationRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository personRepository;
    private final WorkParticipationRepository workParticipationRepository;
    private final MovieRepository movieRepository;

    /**
     * [POST] 새로운 영화 인물(배우, 감독 등) 등록
     */
    @Transactional
    public Person createPerson(PersonCreateRequest request) {
        Person person = Person.create(
                request.type(),
                request.name(),
                request.englishName(),
                request.birthDate(),
                request.award()
        );

        return personRepository.save(person);
    }

    /**
     * [POST] 인물을 특정 영화에 참여시키기
     */
    @Transactional
    public WorkParticipation addWorkParticipation(WorkParticipationRequest request) {
        Movie movie = findMovie(request.movieId());
        Person person = findPerson(request.personId());
        WorkParticipation participation = WorkParticipation.create(movie, person, request.role());

        return workParticipationRepository.save(participation);
    }

    private Movie findMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }

    private Person findPerson(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERSON_NOT_FOUND));
    }

    /**
     * [GET] 특정 영화의 감독/출연진 목록 조회
     */
    public List<WorkParticipation> getParticipantsByMovieId(Long movieId) {
        return workParticipationRepository.findByMovieId(movieId);
    }

    /**
     * [GET] 특정 인물(배우/감독)의 참여 영화 목록(필모그래피) 조회
     */
    public List<WorkParticipation> getParticipationsByPersonId(Long personId) {
        return workParticipationRepository.findByPersonId(personId);
    }
}
