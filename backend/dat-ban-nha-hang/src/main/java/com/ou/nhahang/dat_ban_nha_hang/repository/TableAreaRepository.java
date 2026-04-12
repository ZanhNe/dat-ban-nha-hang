package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableAreaRepository extends JpaRepository<TableArea, Long> {
    List<TableArea> findByRestaurantId(Long restaurantId);
    Optional<TableArea> findByIdAndRestaurantId(Long id, Long restaurantId);
}
