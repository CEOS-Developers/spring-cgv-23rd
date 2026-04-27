package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.*;
import com.ceos.spring_cgv_23rd.domain.theater.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TheaterPersistenceMapper {

    // Entity → Domain

    public Theater toDomain(TheaterEntity entity) {
        return Theater.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .isOpened(entity.isOpened())
                .build();
    }

    public Hall toDomain(HallEntity entity) {
        return Hall.builder()
                .id(entity.getId())
                .theaterId(entity.getTheater().getId())
                .hallType(toDomain(entity.getHallType()))
                .name(entity.getName())
                .build();
    }

    public HallType toDomain(HallTypeEntity entity) {
        return HallType.builder()
                .id(entity.getId())
                .name(entity.getName())
                .totalRows(entity.getTotalRows())
                .totalCols(entity.getTotalCols())
                .build();
    }

    public Seat toDomain(SeatEntity entity) {
        return Seat.builder()
                .id(entity.getId())
                .hallType(toDomain(entity.getHallType()))
                .rowNum(entity.getRowNum())
                .colNum(entity.getColNum())
                .isUsable(entity.getIsUsable())
                .build();
    }

    public TheaterLike toDomain(TheaterLikeEntity entity) {
        return TheaterLike.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .theaterId(entity.getTheater().getId())
                .build();
    }


    // List 변환

    public List<Theater> toDomainTheaters(List<TheaterEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }


    // Domain → Entity

    public TheaterLikeEntity toEntity(TheaterEntity theaterEntity, Long userId) {
        return TheaterLikeEntity.builder()
                .userId(userId)
                .theater(theaterEntity)
                .build();
    }
}
