package com.ou.nhahang.dat_ban_nha_hang.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

        @Query(value = """
                        SELECT * FROM restaurant r
                        WHERE ST_Distance_Sphere(r.location, ST_GeomFromText(:pointWkt, 4326)) <= :radiusInMeters * 1000
                        """, countQuery = """
                        SELECT count(*) FROM restaurant r
                        WHERE ST_Distance_Sphere(r.location, ST_GeomFromText(:pointWkt, 4326)) <= :radiusInMeters * 1000
                        """, nativeQuery = true)
        Page<Restaurant> findNearByRestaurant(@Param("pointWkt") String pointWkt, @Param("radiusInMeters") int radius,
                        Pageable pageable);

        Optional<Restaurant> findById(Long id);

        Optional<Restaurant> findByIdAndManagerId(Long id, Long managerId);

        @Query("""
                        SELECT r FROM Restaurant r
                        WHERE (:status IS NULL OR r.status = :status)
                          AND (:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(r.address) LIKE LOWER(CONCAT('%', :search, '%')))
                        """)
        Page<Restaurant> adminSearchRestaurants(
                        @Param("status") Restaurant.RestaurantStatus status,
                        @Param("search") String search,
                        Pageable pageable);

        @Query("""
                        SELECT COUNT(r) FROM Restaurant r
                        WHERE (:from IS NULL OR r.createdAt >= :from)
                          AND (:to IS NULL OR r.createdAt < :to)
                        """)
        long countCreatedInRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime toExclusive);
}
