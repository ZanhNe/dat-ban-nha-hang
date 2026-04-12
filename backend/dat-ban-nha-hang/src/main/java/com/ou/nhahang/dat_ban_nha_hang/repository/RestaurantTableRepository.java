package com.ou.nhahang.dat_ban_nha_hang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    @Query("SELECT t FROM RestaurantTable t JOIN FETCH t.tableArea a WHERE a.restaurant.id = :restaurantId")
    List<RestaurantTable> findByRestaurantIdWithArea(@Param("restaurantId") Long restaurantId);

    List<RestaurantTable> findByTableAreaId(Long tableAreaId);

    Optional<RestaurantTable> findByIdAndTableAreaRestaurantId(Long id, Long restaurantId);

}
