package com.ou.nhahang.dat_ban_nha_hang.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

<<<<<<< HEAD
    /**
     * Tìm các bàn còn khả dụng của một nhà hàng tại một thời điểm cụ thể.
     * Ở đây mới lọc theo nhà hàng + trạng thái bàn, logic kiểm tra trùng giờ và số lượng khách sẽ được xử lý thêm ở tầng service.
     */
    @Query("""
        SELECT t
        FROM RestaurantTable t
        JOIN t.tableArea a
        WHERE a.restaurant.id = :restaurantId
          AND t.status = com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable$TableStatus.AVAILABLE
        """)
    List<RestaurantTable> findTablesValidByTimeAndRes(
        @Param("time") LocalTime time,
        @Param("restaurantId") Long restaurantId
    );
}
=======
    @Query("SELECT t FROM RestaurantTable t JOIN FETCH t.tableArea a WHERE a.restaurant.id = :restaurantId")
    List<RestaurantTable> findByRestaurantIdWithArea(@Param("restaurantId") Long restaurantId);
>>>>>>> b35b2f3f82ce015a0b4f2322b569a3429786348f

}
