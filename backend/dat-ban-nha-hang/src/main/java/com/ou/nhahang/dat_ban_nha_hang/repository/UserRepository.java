package com.ou.nhahang.dat_ban_nha_hang.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ou.nhahang.dat_ban_nha_hang.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Page<User> findByWorkplaceId(Long workplaceId, Pageable pageable);

    Optional<User> findByIdAndWorkplaceId(Long id, Long workplaceId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.workplace.id = :workplaceId AND r.name = :roleName")
    List<User> findByWorkplaceIdAndRoleName(@Param("workplaceId") Long workplaceId, @Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findAllByRoleName(@Param("roleName") String roleName);

    @Query("""
            SELECT COUNT(u) FROM User u
            WHERE (:from IS NULL OR u.createdAt >= :from)
              AND (:to IS NULL OR u.createdAt < :to)
            """)
    long countCreatedInRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime toExclusive);

    @Query(value = """
            SELECT DISTINCT u FROM User u
            JOIN u.roles r
            WHERE (:roleName IS NULL OR r.name = :roleName)
              AND (:restaurantId IS NULL OR u.workplace.id = :restaurantId)
              AND (:status IS NULL OR u.status = :status)
              AND (:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
            SELECT COUNT(DISTINCT u) FROM User u
            JOIN u.roles r
            WHERE (:roleName IS NULL OR r.name = :roleName)
              AND (:restaurantId IS NULL OR u.workplace.id = :restaurantId)
              AND (:status IS NULL OR u.status = :status)
              AND (:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<User> adminSearchUsers(
            @Param("roleName") String roleName,
            @Param("restaurantId") Long restaurantId,
            @Param("status") User.UserStatus status,
            @Param("search") String search,
            Pageable pageable);
}
