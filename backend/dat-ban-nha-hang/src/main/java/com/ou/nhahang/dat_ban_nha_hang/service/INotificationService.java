package com.ou.nhahang.dat_ban_nha_hang.service;

import java.util.Map;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.NotificationResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.NotificationType;

public interface INotificationService {

        CursorPaginationResult<NotificationResponseDTO> getNotifications(Long userId, Long cursor, int limit);

        long getUnreadCount(Long userId);

        void markAsRead(Long userId, Long notificationId);

        void markAllAsRead(Long userId);

        void sendNotificationToUser(Long userId, String title, String message, NotificationType type,
                        Map<String, Object> metadata);

        void sendBroadcastNotification(String title, String message, NotificationType type,
                        Map<String, Object> metadata);
}
