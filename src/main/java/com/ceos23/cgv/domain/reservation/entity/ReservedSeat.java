package com.ceos23.cgv.domain.reservation.entity;

import com.ceos23.cgv.domain.movie.entity.Screening;
import jakarta.persistence.*;
import lombok.*;

@Entity
// 같은 상영일정에 같은 좌석이 중복 저장되는 것을 DB단에서 차단
@Table(name = "reserved_seats", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_screening_seat",
                columnNames = {"screening_id", "seat_row", "seat_col"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReservedSeat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserved_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "seat_row", nullable = false, length = 2)
    private String seatRow;

    @Column(name = "seat_col", nullable = false)
    private int seatCol;

    // 새로 추가할 생성자 (Rich Domain 구조에서 사용)
    public ReservedSeat(Reservation reservation, String seatNumber) {
        this.reservation = reservation;

        // Screening 매핑: 예매 정보가 속한 상영 일정을 그대로 가져와 연결합니다.
        this.screening = reservation.getScreening();

        // 좌석 번호 검증 방어 로직 (예: null이거나 길이가 너무 짧은 경우)
        if (seatNumber == null || seatNumber.length() < 2) {
            throw new IllegalArgumentException("잘못된 좌석 번호 형식입니다. (예: A1)");
        }

        // 좌석 번호 파싱 로직 (예: "A12" 입력 시 -> Row: "A", Col: 12)
        // 첫 번째 글자는 행(Row), 두 번째 글자부터는 열(Col)로 분리합니다.
        this.seatRow = seatNumber.substring(0, 1).toUpperCase();

        try {
            this.seatCol = Integer.parseInt(seatNumber.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("좌석 번호의 열(Column)은 숫자여야 합니다. 입력값: " + seatNumber);
        }
    }
}
