package com.ceos23.spring_cgv_23rd.Payment.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ReservationSeat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ID_GENERATOR_UNIQUE",
                        columnNames = {"date", "seq"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentId {
    @Id
    private LocalDate date;

    private int seq;

    public PaymentId(LocalDate now, int i) {
        this.date = now;
        this.seq = i;
    }

    public String getCompletePaymentId(){
        String dateNow = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return dateNow + "_" + String.format("%04d",seq);
    }

    public void setNextSeq(){
        this.seq++;
    }
}
