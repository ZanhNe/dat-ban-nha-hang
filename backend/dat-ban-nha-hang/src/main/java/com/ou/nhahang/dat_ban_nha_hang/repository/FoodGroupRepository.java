package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.FoodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodGroupRepository extends JpaRepository<FoodGroup, Long> {
    List<FoodGroup> findByMenuId(Long menuId);
    Optional<FoodGroup> findByIdAndMenuRestaurantId(Long id, Long restaurantId);
}
