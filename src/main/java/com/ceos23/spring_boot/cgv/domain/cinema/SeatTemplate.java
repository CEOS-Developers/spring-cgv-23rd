package com.ceos23.spring_boot.cgv.domain.cinema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_template_layout_row_col",
                        columnNames = {"seat_layout_id", "row_name", "col_number"}
                )
        }
)
public class SeatTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "row_name", nullable = false, length = 10)
    private String rowName;

    @Column(name = "col_number", nullable = false)
    private Integer colNumber;

    @ManyToOne
    @JoinColumn(name = "seat_layout_id", nullable = false)
    private SeatLayout seatLayout;

    public SeatTemplate(String rowName, Integer colNumber, SeatLayout seatLayout) {
        this.rowName = rowName;
        this.colNumber = colNumber;
        this.seatLayout = seatLayout;
    }

    public boolean belongsTo(Long seatLayoutId) {
        return seatLayout.getId().equals(seatLayoutId);
    }
}
