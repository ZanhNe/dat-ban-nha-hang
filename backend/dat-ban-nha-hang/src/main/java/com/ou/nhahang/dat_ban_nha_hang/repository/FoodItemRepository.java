package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.FoodItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    interface TopFoodProjection {
        Long getFoodId();
        String getFoodName();
        Long getQuantitySold();
        Long getRevenue();
    }

    @Query("""
            SELECT fd.id AS foodId,
                   fd.name AS foodName,
                   COALESCE(SUM(fi.quantity), 0) AS quantitySold,
                   COALESCE(SUM(fi.quantity * fd.price), 0) AS revenue
            FROM FoodItem fi
            JOIN fi.foodDescription fd
            JOIN fi.foodOrder fo
            JOIN fo.tableSession ts
            JOIN ts.table t
            JOIN t.tableArea ta
            WHERE ta.restaurant.id = :restaurantId
              AND fi.status = com.ou.nhahang.dat_ban_nha_hang.entity.FoodItem$FoodItemStatus.SERVED
              AND (:from IS NULL OR fi.createdAt >= :from)
              AND (:to IS NULL OR fi.createdAt < :to)
            GROUP BY fd.id, fd.name
            ORDER BY quantitySold DESC
            """)
    List<TopFoodProjection> findTopFoodsByRestaurant(
            @Param("restaurantId") Long restaurantId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime toExclusive,
            Pageable pageable);
}

