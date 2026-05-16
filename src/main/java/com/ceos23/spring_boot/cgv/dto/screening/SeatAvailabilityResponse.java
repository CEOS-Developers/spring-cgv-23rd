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
            List<SeatTemplateSnapshot> seatTemplates,
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
                                reservedSeatTemplateIds.contains(seatTemplate.seatTemplateId())
                        ))
                        .toList()
        );
    }

    public record SeatTemplateSnapshot(
            Long seatTemplateId,
            String rowName,
            Integer colNumber,
            String seatNumber
    ) {
        public static SeatTemplateSnapshot from(SeatTemplate seatTemplate) {
            return new SeatTemplateSnapshot(
                    seatTemplate.getId(),
                    seatTemplate.getRowName(),
                    seatTemplate.getColNumber(),
                    seatTemplate.getSeatNumber()
            );
        }
    }

    public record SeatStatusResponse(
            Long seatTemplateId,
            String rowName,
            Integer colNumber,
            String seatNumber,
            boolean reserved
    ) {
        public static SeatStatusResponse of(SeatTemplateSnapshot seatTemplate, boolean reserved) {
            return new SeatStatusResponse(
                    seatTemplate.seatTemplateId(),
                    seatTemplate.rowName(),
                    seatTemplate.colNumber(),
                    seatTemplate.seatNumber(),
                    reserved
            );
        }
    }
}
