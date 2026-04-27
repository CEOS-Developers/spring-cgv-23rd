package com.ceos23.cgv.domain.event.service;

import com.ceos23.cgv.domain.event.dto.EventCreateRequest;
import com.ceos23.cgv.domain.event.entity.Event;
import com.ceos23.cgv.domain.event.entity.MovieEvent;
import com.ceos23.cgv.domain.event.repository.EventRepository;
import com.ceos23.cgv.domain.event.repository.MovieEventRepository;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final MovieEventRepository movieEventRepository;
    private final MovieRepository movieRepository;

    /**
     * [POST] 새로운 이벤트 생성
     */
    @Transactional
    public Event createEvent(EventCreateRequest request) {
        Event event = Event.create(
                request.title(),
                request.content(),
                request.startDate(),
                request.endDate()
        );

        return eventRepository.save(event);
    }

    /**
     * [POST] 특정 이벤트를 특정 영화와 연결
     */
    @Transactional
    public MovieEvent linkEventToMovie(Long eventId, Long movieId) {
        Event event = findEvent(eventId);
        Movie movie = findMovie(movieId);
        MovieEvent movieEvent = MovieEvent.link(event, movie);

        return movieEventRepository.save(movieEvent);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
    }

    private Movie findMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }

    /**
     * [GET] 전체 진행 중인 이벤트 목록 조회
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * [GET] 특정 영화와 관련된 이벤트만 조회
     */
    public List<Event> getEventsByMovieId(Long movieId) {
        // MovieEvent(중간 테이블) 목록을 가져와서 Event 객체만 추출하여 반환
        return movieEventRepository.findByMovieId(movieId).stream()
                .map(MovieEvent::getEvent)
                .collect(Collectors.toList());
    }
}
