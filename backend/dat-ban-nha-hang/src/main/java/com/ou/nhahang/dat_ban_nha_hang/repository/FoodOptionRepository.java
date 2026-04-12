package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.FoodOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodOptionRepository extends JpaRepository<FoodOption, Long> {
    List<FoodOption> findByOptionGroupId(Long optionGroupId);
    Optional<FoodOption> findByIdAndOptionGroupRestaurantId(Long id, Long restaurantId);
}
