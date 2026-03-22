package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("""
        SELECT r FROM Review r 
        JOIN FETCH r.user u
        WHERE r.restaurant.id = :restaurantId
          AND (:rating IS NULL OR r.rating = :rating)
          AND (
              (:cursor IS NULL) OR 
              (:sort = 'NEWEST' AND r.id < :cursor) OR 
              (:sort = 'OLDEST' AND r.id > :cursor)
          )
        ORDER BY 
          CASE WHEN :sort = 'NEWEST' THEN r.id END DESC,
          CASE WHEN :sort = 'OLDEST' THEN r.id END ASC
    """)
    List<Review> findReviewsByCursor(
        @Param("restaurantId") Long restaurantId,
        @Param("rating") Integer rating,
        @Param("cursor") Long cursor,
        @Param("sort") String sort,
        Pageable pageable
    );

    long countByRestaurantId(Long restaurantId);

    boolean existsByRestaurantIdAndUserId(Long restaurantId, Long userId);
}
