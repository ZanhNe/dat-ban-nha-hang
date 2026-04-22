package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.NotificationResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Notification;
import com.ou.nhahang.dat_ban_nha_hang.entity.NotificationType;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.NotificationRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

        private final NotificationRepository notificationRepository;
        private final SimpMessagingTemplate messagingTemplate;

        @Override
        @Transactional
        public void sendNotificationToUser(Long userId,
                        String title,
                        String content,
                        NotificationType type,
                        Map<String, Object> metadata) {
                Notification notification = Notification.builder()
                                .userId(userId)
                                .title(title)
                                .content(content)
                                .type(type)
                                .metadata(metadata)
                                .build();

                Notification savedNotification = notificationRepository.save(notification);

                NotificationResponseDTO response = NotificationResponseDTO.builder()
                                .id(savedNotification.getId())
                                .title(savedNotification.getTitle())
                                .content(savedNotification.getContent())
                                .type(savedNotification.getType())
                                .read(savedNotification.isRead())
                                .metadata(savedNotification.getMetadata())
                                .createdAt(savedNotification.getCreatedAt())
                                .build();

                messagingTemplate.convertAndSendToUser(
                                userId.toString(),
                                "/queue/notifications",
                                response);
        }

        @Override
        @Transactional(readOnly = true)
        public CursorPaginationResult<NotificationResponseDTO> getNotifications(Long userId, Long cursor, int limit) {
                int fetchLimit = limit + 1; // Chỗ này tui cho lấy thêm 1 để check có next hay không nè
                Pageable pageable = PageRequest.of(0, fetchLimit);

                Page<Notification> notificationPage = notificationRepository.findNotificationsByUserIdAndCursor(userId,
                                cursor,
                                pageable);
                List<Notification> notifications = notificationPage.getContent();

                boolean hasMore = notifications.size() > limit;
                if (hasMore) {
                        notifications = notifications.subList(0, limit);
                }

                Long nextCursor = notifications.isEmpty() ? null : notifications.get(notifications.size() - 1).getId();
                long totalElements = notificationRepository.countByUserId(userId);

                List<Long> broadcastIds = notifications.stream()
                                .filter(n -> n.getUserId() == null)
                                .map(Notification::getId)
                                .collect(Collectors.toList());
                Set<Long> readBroadcastIds = broadcastIds.isEmpty()
                                ? new HashSet<>()
                                : notificationRepository.findReadBroadcastIds(userId, broadcastIds);

                List<NotificationResponseDTO> data = notifications.stream()
                                .map(n -> NotificationResponseDTO.builder()
                                                .id(n.getId())
                                                .title(n.getTitle())
                                                .content(n.getContent())
                                                .type(n.getType())
                                                .read(n.getUserId() != null ? n.isRead()
                                                                : readBroadcastIds.contains(n.getId()))
                                                .metadata(n.getMetadata())
                                                .createdAt(n.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                CursorPaginationResult.CursorPaginationMeta meta = CursorPaginationResult.CursorPaginationMeta.builder()
                                .nextCursor(nextCursor)
                                .hasMore(hasMore)
                                .totalElements(totalElements)
                                .build();
                return CursorPaginationResult.<NotificationResponseDTO>builder()
                                .data(data)
                                .meta(meta)
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public long getUnreadCount(Long userId) {
                return notificationRepository.countByUserIdAndReadFalse(userId);
        }

        @Override
        @Transactional
        public void markAsRead(Long userId, Long notificationId) {
                int updated = notificationRepository.markAsRead(userId, notificationId);
                if (updated == 0) {
                        updated = notificationRepository.markBroadcastAsRead(userId, notificationId);
                }
                if (updated == 0) {
                        throw new ResourceNotFoundException(
                                        "Không tìm thấy thông báo hoặc thông báo không thuộc về bạn");
                }
        }

        @Override
        @Transactional
        public void markAllAsRead(Long userId) {
                notificationRepository.markAllAsRead(userId);
                notificationRepository.markAllBroadcastsAsRead(userId);
        }

        @Override
        @Transactional
        public void sendBroadcastNotification(String title, String content, NotificationType type,
                        Map<String, Object> metadata) {
                Notification notification = Notification.builder()
                                .userId(null) // Đánh dấu là Broadcast
                                .title(title)
                                .content(content)
                                .type(type)
                                .metadata(metadata)
                                .build();

                Notification savedNotification = notificationRepository.save(notification);

                NotificationResponseDTO response = NotificationResponseDTO.builder()
                                .id(savedNotification.getId())
                                .title(savedNotification.getTitle())
                                .content(savedNotification.getContent())
                                .type(savedNotification.getType())
                                .read(false)
                                .metadata(savedNotification.getMetadata())
                                .createdAt(savedNotification.getCreatedAt())
                                .build();

                String destination = "/topic/notifications";
                messagingTemplate.convertAndSend(destination, response);
        }
}
