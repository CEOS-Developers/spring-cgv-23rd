package cgv_23rd.ceos.repository;

import cgv_23rd.ceos.entity.enums.FoodOrderStatus;
import cgv_23rd.ceos.entity.food.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
            "JOIN FETCH fo.theater t " +
            "LEFT JOIN FETCH fo.foodOrderItems foi " +
            "LEFT JOIN FETCH foi.food f " +
            "WHERE fo.user.id = :userId")
    List<FoodOrder> findAllByUserIdWithDetails(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update FoodOrder fo
           set fo.status = :canceledStatus
         where fo.status = :pendingStatus
           and fo.createdAt < :expiredAt
    """)
    int expirePendingFoodOrders(
            @Param("pendingStatus") FoodOrderStatus pendingStatus,
            @Param("canceledStatus") FoodOrderStatus canceledStatus,
            @Param("expiredAt") LocalDateTime expiredAt
    );

}
