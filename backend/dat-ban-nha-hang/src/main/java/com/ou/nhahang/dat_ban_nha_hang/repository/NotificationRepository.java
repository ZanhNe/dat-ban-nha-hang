package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE (n.userId = :userId OR n.userId IS NULL) AND (:cursor IS NULL OR n.id < :cursor) ORDER BY n.id DESC")
    Page<Notification> findNotificationsByUserIdAndCursor(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId OR n.userId IS NULL")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE " +
           "(n.userId = :userId AND n.read = false) OR " +
           "(n.userId IS NULL AND n.id NOT IN (SELECT urn.notification.id FROM UserReadNotification urn WHERE urn.user.id = :userId))")
    long countByUserIdAndReadFalse(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.id = :notificationId")
    int markAsRead(@Param("userId") Long userId, @Param("notificationId") Long notificationId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    int markAllAsRead(@Param("userId") Long userId);

    @Modifying
    @Query(value = "INSERT INTO user_read_notification (user_id, notification_id, read_at) " +
                   "SELECT :userId, n.id, CURRENT_TIMESTAMP FROM notifications n " +
                   "WHERE n.user_id IS NULL AND n.id = :notificationId " +
                   "AND n.id NOT IN (SELECT urn.notification_id FROM user_read_notification urn WHERE urn.user_id = :userId)", 
           nativeQuery = true)
    int markBroadcastAsRead(@Param("userId") Long userId, @Param("notificationId") Long notificationId);

    @Modifying
    @Query(value = "INSERT INTO user_read_notification (user_id, notification_id, read_at) " +
                   "SELECT :userId, n.id, CURRENT_TIMESTAMP FROM notifications n " +
                   "WHERE n.user_id IS NULL " +
                   "AND n.id NOT IN (SELECT urn.notification_id FROM user_read_notification urn WHERE urn.user_id = :userId)", 
           nativeQuery = true)
    int markAllBroadcastsAsRead(@Param("userId") Long userId);

    @Query(value = "SELECT notification_id FROM user_read_notification WHERE user_id = :userId AND notification_id IN :notificationIds", nativeQuery = true)
    java.util.Set<Long> findReadBroadcastIds(@Param("userId") Long userId, @Param("notificationIds") java.util.List<Long> notificationIds);
}
