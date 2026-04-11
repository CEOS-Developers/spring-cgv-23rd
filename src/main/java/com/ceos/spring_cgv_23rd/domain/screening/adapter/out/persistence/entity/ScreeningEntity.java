package com.ceos.spring_cgv_23rd.domain.screening.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity.MovieEntity;
import com.ceos.spring_cgv_23rd.domain.screening.exception.ScreeningErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.HallEntity;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "screening")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScreeningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private HallEntity hall;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "remaining_seats", nullable = false)
    private Integer remainingSeats;

    @Column(name = "price", nullable = false)
    private Integer price;


    // TODO: 삭제
    public void decreaseRemainingSeats(int count) {
        if (this.remainingSeats < count) {
            throw new GeneralException(ScreeningErrorCode.NO_REMAINING_SEATS);
        }

        this.remainingSeats -= count;
    }

    // TODO: 삭제
    public void increaseRemainingSeats(int count) {
        if (this.remainingSeats + count > this.totalSeats) {
            throw new GeneralException(ScreeningErrorCode.INVALID_SEAT_COUNT);
        }
        this.remainingSeats += count;
    }
}
