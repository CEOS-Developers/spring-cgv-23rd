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

    public static ReservedSeat create(Reservation reservation, Screening screening, String seatRow, int seatCol) {
        return ReservedSeat.builder()
                .reservation(reservation)
                .screening(screening)
                .seatRow(seatRow)
                .seatCol(seatCol)
                .build();
    }
}
