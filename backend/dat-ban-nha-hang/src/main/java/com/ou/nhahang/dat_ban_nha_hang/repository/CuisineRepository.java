package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuisineRepository extends JpaRepository<Cuisine, Long> {
    Optional<Cuisine> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}

