package com.ou.nhahang.dat_ban_nha_hang.controller;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.NotificationResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getCredentials();
        CursorPaginationResult<NotificationResponseDTO> result = notificationService.getNotifications(userId, cursor, limit);

        ApiResponse<List<NotificationResponseDTO>> response = ApiResponse.<List<NotificationResponseDTO>>builder()
                .status(200)
                .message("Lấy danh sách thông báo thành công")
                .data(result.data())
                .meta(result.meta())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        long unreadCount = notificationService.getUnreadCount(userId);

        ApiResponse<Map<String, Long>> response = ApiResponse.<Map<String, Long>>builder()
                .status(200)
                .message("Lấy số lượng thông báo chưa đọc thành công")
                .data(Map.of("unreadCount", unreadCount))
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("id") Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getCredentials();
        notificationService.markAsRead(userId, id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã đánh dấu thông báo là đã đọc")
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        notificationService.markAllAsRead(userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã đánh dấu tất cả thông báo là đã đọc")
                .build();

        return ResponseEntity.ok(response);
    }
}
