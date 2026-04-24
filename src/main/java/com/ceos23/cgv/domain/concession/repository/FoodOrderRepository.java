package com.ceos23.cgv.domain.concession.repository;

import com.ceos23.cgv.domain.concession.entity.FoodOrder;
import com.ceos23.cgv.domain.concession.enums.FoodOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    // Fetch Join을 적용하여 User와 Cinema를 한 번의 쿼리로 가져옵니다
    @Query("SELECT f FROM FoodOrder f " +
            "JOIN FETCH f.user " +
            "JOIN FETCH f.cinema " +
            "WHERE f.user.id = :userId AND f.status = :status")
    List<FoodOrder> findByUserIdAndStatusWithFetchJoin(@Param("userId") Long userId,
                                                       @Param("status") FoodOrderStatus status);

    Optional<FoodOrder> findByPaymentId(String paymentId);
}
