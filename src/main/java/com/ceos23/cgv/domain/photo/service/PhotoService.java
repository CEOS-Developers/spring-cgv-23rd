package com.ceos23.cgv.domain.photo.service;

import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.person.entity.Person;
import com.ceos23.cgv.domain.person.repository.PersonRepository;
import com.ceos23.cgv.domain.photo.dto.PhotoCreateRequest;
import com.ceos23.cgv.domain.photo.entity.Photo;
import com.ceos23.cgv.domain.photo.repository.PhotoRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;

    /**
     * [POST] 사진 등록 (영화 사진 or 인물 사진)
     */
    @Transactional
    public Photo createPhoto(PhotoCreateRequest request) {
        Movie movie = findMovieOrNull(request.movieId());
        Person person = findPersonOrNull(request.personId());
        Photo photo = Photo.create(request.name(), movie, person);

        return photoRepository.save(photo);
    }

    private Movie findMovieOrNull(Long movieId) {
        if (movieId == null) {
            return null;
        }

        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }

    private Person findPersonOrNull(Long personId) {
        if (personId == null) {
            return null;
        }

        return personRepository.findById(personId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERSON_NOT_FOUND));
    }

    /**
     * [GET] 특정 영화의 사진 목록 조회
     */
    public List<Photo> getPhotosByMovieId(Long movieId) {
        return photoRepository.findByMovieId(movieId);
    }

    /**
     * [GET] 특정 인물의 사진 목록 조회
     */
    public List<Photo> getPhotosByPersonId(Long personId) {
        return photoRepository.findByPersonId(personId);
    }
}
