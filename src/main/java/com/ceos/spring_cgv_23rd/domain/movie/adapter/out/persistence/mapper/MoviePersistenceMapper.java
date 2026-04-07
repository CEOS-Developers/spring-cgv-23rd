package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.*;
import com.ceos.spring_cgv_23rd.domain.movie.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MoviePersistenceMapper {

    //  Entity → Domain

    public Movie toDomain(MovieEntity entity) {
        return Movie.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .prolog(entity.getProlog())
                .status(entity.getStatus())
                .duration(entity.getDuration())
                .genre(entity.getGenre())
                .ageRating(entity.getAgeRating())
                .releasedAt(entity.getReleasedAt())
                .posterUrl(entity.getPosterUrl())
                .movieStatistic(toDomain(entity.getMovieStatistic()))
                .build();
    }

    public MovieStatistic toDomain(MovieStatisticEntity entity) {
        return MovieStatistic.builder()
                .id(entity.getId())
                .reservationRate(entity.getReservationRate())
                .reservationRank(entity.getReservationRank())
                .viewCount(entity.getViewCount())
                .eggCount(entity.getEggCount())
                .maleReservationRate(entity.getMaleReservationRate())
                .femaleReservationRate(entity.getFemaleReservationRate())
                .age10sRate(entity.getAge10sRate())
                .age20sRate(entity.getAge20sRate())
                .age30sRate(entity.getAge30sRate())
                .age40sRate(entity.getAge40sRate())
                .age50sRate(entity.getAge50sRate())
                .build();
    }

    public MovieCredit toDomain(MovieCreditEntity entity) {
        return MovieCredit.builder()
                .id(entity.getId())
                .movieId(entity.getMovie().getId())
                .contributor(toDomain(entity.getContributor()))
                .roleType(entity.getRoleType())
                .build();
    }

    public Contributor toDomain(ContributorEntity entity) {
        return Contributor.builder()
                .id(entity.getId())
                .name(entity.getName())
                .profileImageUrl(entity.getProfileImageUrl())
                .build();
    }

    public MovieMedia toDomain(MovieMediaEntity entity) {
        return MovieMedia.builder()
                .id(entity.getId())
                .movieId(entity.getMovie().getId())
                .mediaType(entity.getMediaType())
                .mediaUrl(entity.getMediaUrl())
                .build();
    }

    public MovieLike toDomain(MovieLikeEntity entity) {
        return MovieLike.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .movieId(entity.getMovie().getId())
                .build();
    }


    // List 변환

    public List<Movie> toDomainMovies(List<MovieEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public List<MovieCredit> toDomainCredits(List<MovieCreditEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public List<MovieMedia> toDomainMedias(List<MovieMediaEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }


    // Domain → Entity

    public MovieLikeEntity toEntity(MovieEntity movieEntity, Long userId) {
        return MovieLikeEntity.builder()
                .userId(userId)
                .movie(movieEntity)
                .build();
    }
}
