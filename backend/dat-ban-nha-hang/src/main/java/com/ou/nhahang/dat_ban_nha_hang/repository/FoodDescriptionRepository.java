package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.FoodDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodDescriptionRepository extends JpaRepository<FoodDescription, Long> {
    List<FoodDescription> findByFoodGroupId(Long foodGroupId);
    Optional<FoodDescription> findByIdAndFoodGroupMenuRestaurantId(Long id, Long restaurantId);
}
