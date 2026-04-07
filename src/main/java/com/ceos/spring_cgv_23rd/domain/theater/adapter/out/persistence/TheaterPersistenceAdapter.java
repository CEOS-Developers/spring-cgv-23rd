package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterLikeEntity;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.mapper.TheaterPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.TheaterJpaRepository;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.repository.TheaterLikeJpaRepository;
import com.ceos.spring_cgv_23rd.domain.theater.application.port.out.TheaterPersistencePort;
import com.ceos.spring_cgv_23rd.domain.theater.domain.Theater;
import com.ceos.spring_cgv_23rd.domain.theater.domain.TheaterLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TheaterPersistenceAdapter implements TheaterPersistencePort {

    private final TheaterJpaRepository theaterJpaRepository;
    private final TheaterLikeJpaRepository theaterLikeJpaRepository;
    private final TheaterPersistenceMapper mapper;


    @Override
    public List<Theater> findAllTheaters() {
        return mapper.toDomainTheaters(theaterJpaRepository.findAll());
    }

    @Override
    public Optional<Theater> findTheaterById(Long theaterId) {
        return theaterJpaRepository.findById(theaterId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsTheaterById(Long theaterId) {
        return theaterJpaRepository.existsById(theaterId);
    }


    @Override
    public Optional<TheaterLike> findTheaterLikeByUserAndTheater(Long userId, Long theaterId) {
        return theaterLikeJpaRepository.findByUserIdAndTheaterId(userId, theaterId)
                .map(mapper::toDomain);
    }

    @Override
    public TheaterLike saveTheaterLike(TheaterLike theaterLike) {
        TheaterEntity theaterEntity = theaterJpaRepository.getReferenceById(theaterLike.getTheaterId());

        TheaterLikeEntity saved = theaterLikeJpaRepository.save(mapper.toEntity(theaterEntity, theaterLike.getUserId()));

        return mapper.toDomain(saved);
    }

    @Override
    public void deleteTheaterLike(TheaterLike theaterLike) {
        theaterLikeJpaRepository.deleteById(theaterLike.getId());
    }
}
