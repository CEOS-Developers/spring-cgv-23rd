package com.ceos.spring_cgv_23rd.domain.screening.domain;

import com.ceos.spring_cgv_23rd.domain.screening.exception.ScreeningErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Screening {

    private Long id;
    private Long movieId;
    private Long hallId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer totalSeats;
    private Integer remainingSeats;
    private Integer price;


    public void decreaseRemainingSeats(int count) {
        if (this.remainingSeats < count) {
            throw new GeneralException(ScreeningErrorCode.NO_REMAINING_SEATS);
        }

        this.remainingSeats -= count;
    }

    public void increaseRemainingSeats(int count) {
        if (this.remainingSeats + count > this.totalSeats) {
            throw new GeneralException(ScreeningErrorCode.INVALID_SEAT_COUNT);
        }
        this.remainingSeats += count;
    }
}
