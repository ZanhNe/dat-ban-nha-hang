package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.FoodOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodOptionGroupRepository extends JpaRepository<FoodOptionGroup, Long> {
    List<FoodOptionGroup> findByRestaurantId(Long restaurantId);
    Optional<FoodOptionGroup> findByIdAndRestaurantId(Long id, Long restaurantId);
    List<FoodOptionGroup> findByIdInAndRestaurantId(List<Long> ids, Long restaurantId);
}
