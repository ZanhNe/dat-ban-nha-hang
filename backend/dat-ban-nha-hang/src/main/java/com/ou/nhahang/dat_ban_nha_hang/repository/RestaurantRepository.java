package com.ou.nhahang.dat_ban_nha_hang.repository;

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
                        WHERE ST_Distance_Sphere(r.location, ST_GeomFromText(:pointWkt, 4326)) <= :radiusInMeters
                        """, countQuery = """
                        SELECT count(*) FROM restaurant r
                        WHERE ST_Distance_Sphere(r.location, ST_GeomFromText(:pointWkt, 4326)) <= :radiusInMeters
                        """, nativeQuery = true)
        Page<Restaurant> findNearByRestaurant(@Param("pointWkt") String pointWkt, @Param("radiusInMeters") int radius,
                        Pageable pageable);
}
