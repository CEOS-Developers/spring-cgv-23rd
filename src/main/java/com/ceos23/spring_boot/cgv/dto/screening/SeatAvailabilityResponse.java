package com.ceos23.spring_boot.cgv.dto.screening;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import java.util.List;
import java.util.Set;

public record SeatAvailabilityResponse(
        Long screeningId,
        Long seatLayoutId,
        String seatLayoutName,
        Integer rowsCount,
        Integer colsCount,
        List<SeatStatusResponse> seats
) {
    public static SeatAvailabilityResponse of(
            Screening screening,
            List<SeatTemplate> seatTemplates,
            Set<Long> reservedSeatTemplateIds
    ) {
        SeatLayout seatLayout = screening.getScreen().getSeatLayout();

        return new SeatAvailabilityResponse(
                screening.getId(),
                seatLayout.getId(),
                seatLayout.getName(),
                seatLayout.getRowsCount(),
                seatLayout.getColsCount(),
                seatTemplates.stream()
                        .map(seatTemplate -> SeatStatusResponse.of(
                                seatTemplate,
                                reservedSeatTemplateIds.contains(seatTemplate.getId())
                        ))
                        .toList()
        );
    }

    public record SeatStatusResponse(
            Long seatTemplateId,
            String rowName,
            Integer colNumber,
            String seatNumber,
            boolean reserved
    ) {
        public static SeatStatusResponse of(SeatTemplate seatTemplate, boolean reserved) {
            return new SeatStatusResponse(
                    seatTemplate.getId(),
                    seatTemplate.getRowName(),
                    seatTemplate.getColNumber(),
                    seatTemplate.getSeatNumber(),
                    reserved
            );
        }
    }
}
