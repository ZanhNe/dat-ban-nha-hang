package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTableSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantTableSessionRepository extends JpaRepository<RestaurantTableSession, Long> {

    @Query("""
            SELECT s FROM RestaurantTableSession s
            JOIN s.booking b
            WHERE b.restaurant.id = :restaurantId
            AND s.status = :status
            AND (:unassigned IS NULL OR (:unassigned = true AND s.waiter IS NULL) OR (:unassigned = false AND s.waiter IS NOT NULL))
            """)
    Page<RestaurantTableSession> findByRestaurantIdAndStatus(
            @Param("restaurantId") Long restaurantId,
            @Param("status") RestaurantTableSession.TableSessionStatus status,
            @Param("unassigned") Boolean unassigned,
            Pageable pageable);

    @Query("""
            SELECT s FROM RestaurantTableSession s
            JOIN s.booking b
            WHERE b.restaurant.id = :restaurantId
            AND s.status = :status
            """)
    Page<RestaurantTableSession> findByRestaurantIdAndStatus(
            @Param("restaurantId") Long restaurantId,
            @Param("status") RestaurantTableSession.TableSessionStatus status,
            Pageable pageable);

    @Query("""
            SELECT s FROM RestaurantTableSession s
            JOIN s.booking b
            WHERE b.restaurant.id = :restaurantId
            AND s.id = :sessionId
            """)
    Optional<RestaurantTableSession> findByIdAndRestaurantId(
            @Param("sessionId") Long sessionId,
            @Param("restaurantId") Long restaurantId);

    @Query("""
            SELECT s FROM RestaurantTableSession s
            JOIN s.booking b
            WHERE b.restaurant.id = :restaurantId
            AND s.status = :status
            AND s.waiter.id = :waiterId
            """)
    Page<RestaurantTableSession> findByRestaurantIdAndStatusAndWaiterId(
            @Param("restaurantId") Long restaurantId,
            @Param("status") RestaurantTableSession.TableSessionStatus status,
            @Param("waiterId") Long waiterId,
            Pageable pageable);
}
