package com.ou.nhahang.dat_ban_nha_hang.controller.admin;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminNotificationRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final IAdminNotificationService adminNotificationService;

    @PostMapping("/broadcast")
    public ResponseEntity<ApiResponse<Void>> broadcast(@Valid @RequestBody AdminNotificationRequestDTO.Broadcast request) {
        adminNotificationService.broadcast(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã đưa vào hàng đợi gửi thông báo")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendToUser(@Valid @RequestBody AdminNotificationRequestDTO.SendToUser request) {
        adminNotificationService.sendToUser(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã gửi thông báo thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

