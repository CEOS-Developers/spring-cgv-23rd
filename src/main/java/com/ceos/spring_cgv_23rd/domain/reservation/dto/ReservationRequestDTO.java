package com.ceos.spring_cgv_23rd.domain.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ReservationRequestDTO {

    @Builder
    public record CreateReservationRequestDTO(
            @NotNull(message = "상영 ID가 없습니다.")
            Long screeningId,

            @NotEmpty(message = "좌석을 선택해주세요.")
            List<Long> seatIds
    ) {
    }

    @Builder
    public record CreateGuestReservationRequestDTO(
            @NotNull(message = "상영 ID가 없습니다.")
            Long screeningId,

            @NotEmpty(message = "좌석을 선택해주세요.")
            List<Long> seatIds,

            @NotBlank(message = "이름이 없습니다.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
            String guestName,

            @NotBlank(message = "전화번호가 없습니다.")
            @Pattern(regexp = "^010\\d{7,8}$", message = "전화번호 형식이 올바르지 않습니다.")
            String guestPhone,

            @NotNull(message = "생년월일이 없습니다.")
            LocalDate guestBirth,

            @NotBlank(message = "비밀번호가 없습니다.")
            @Size(min = 4, max = 4, message = "비밀번호는 4자리로 입력해주세요.")
            String guestPassword
    ) {
    }

    @Builder
    public record CancelGuestReservationRequestDTO(

            @NotBlank(message = "예매 번호가 없습니다.")
            String reservationNumber,

            @NotBlank(message = "전화번호가 없습니다.")
            String guestPhone,

            @NotNull(message = "생년월일이 없습니다.")
            LocalDate guestBirth,

            @NotBlank(message = "비밀번호가 없습니다.")
            String guestPassword
    ) {
    }
}
