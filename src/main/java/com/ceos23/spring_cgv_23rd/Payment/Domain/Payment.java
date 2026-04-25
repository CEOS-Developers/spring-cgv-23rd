package com.ceos23.spring_cgv_23rd.Payment.Domain;

import com.ceos23.spring_cgv_23rd.Payment.DTO.Currency;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    private Payment(String paymentId, LocalDateTime time, String storeId, String orderName, int totalPayAmount, Currency currency, PayType payType, long targetId){
        this.paymentId = paymentId;
        this.createdAt = time;
        this.storeId = storeId;
        this.orderName = orderName;
        this.totalPayAmount = totalPayAmount;
        this.currency = currency;
        this.payType = payType;
        this.status = ReservationStatus.RESERVED;
        this.targetId = targetId;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String paymentId;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    @Enumerated(value = EnumType.STRING)
    private PayType payType;

    private long targetId;

    private String storeId;

    private String orderName;

    private int totalPayAmount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    public Payment setTarget(long targetId){
        this.targetId = targetId;
        return this;
    }

    public void paymentFail(){
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void paymentSuccess(){
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    public void cancel(){
        this.status = ReservationStatus.CANCELED;
    }

    public static Payment create(String id, String storeId, String orderName, int totalPayAmount, Currency currency, PayType type, long targetId){
        return new Payment(id, LocalDateTime.now(), storeId, orderName, totalPayAmount, currency, type, targetId);
    }

    public void updatePaymentId(String paymentId){
        this.paymentId = paymentId;
    }
}
