package com.ceos23.spring_cgv_23rd.Payment.Domain;

import com.ceos23.spring_cgv_23rd.Payment.DTO.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "USER_UNIQUE",
                        columnNames = {"payment_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    private Payment(String userLoginId, String paymentId, LocalDateTime time, String storeId, String orderName, int totalPayAmount, Currency currency, PayType payType, long targetId){
        this.userLoginId = userLoginId;
        this.paymentId = paymentId;
        this.createdAt = time;
        this.storeId = storeId;
        this.orderName = orderName;
        this.totalPayAmount = totalPayAmount;
        this.currency = currency;
        this.payType = payType;
        this.targetId = targetId;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    private String userLoginId;

    @Getter
    private String paymentId;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    @Getter
    @Enumerated(value = EnumType.STRING)
    private PayType payType;

    @Getter
    private long targetId;

    @Getter
    private String storeId;

    @Getter
    private String orderName;

    @Getter
    private int totalPayAmount;


    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    public boolean isPending(){
        return paymentStatus.equals(PaymentStatus.PENDING);
    }

    public boolean isFailed(){
        return paymentStatus.equals(PaymentStatus.FAILED);
    }

    public void paymentFail(){
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void paymentSuccess(){
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    public void cancel(){
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public static Payment create(String userLoginId, String paymentId, String storeId, String orderName, int totalPayAmount, Currency currency, PayType type, long targetId){
        return new Payment(userLoginId, paymentId, LocalDateTime.now(), storeId, orderName, totalPayAmount, currency, type, targetId);
    }

    public void updatePaymentId(String paymentId){
        this.paymentId = paymentId;
    }
}
