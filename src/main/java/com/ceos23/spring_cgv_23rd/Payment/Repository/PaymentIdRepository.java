package com.ceos23.spring_cgv_23rd.Payment.Repository;

import com.ceos23.spring_cgv_23rd.Payment.Domain.PaymentId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface PaymentIdRepository extends JpaRepository<PaymentId, LocalDate> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentId p where p.date = :date")
    Optional<PaymentId> findForUpdate(@Param("date") LocalDate date);
}
