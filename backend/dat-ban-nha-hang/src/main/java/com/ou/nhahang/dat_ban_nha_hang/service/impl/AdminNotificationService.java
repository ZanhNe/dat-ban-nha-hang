package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminNotificationRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.NotificationType;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminNotificationService;
import com.ou.nhahang.dat_ban_nha_hang.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNotificationService implements IAdminNotificationService {

    private final INotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void broadcast(AdminNotificationRequestDTO.Broadcast request) {
        NotificationType type = NotificationType.valueOf(request.type());
        if ("ALL".equalsIgnoreCase(request.targetRole())) {
            notificationService.sendBroadcastNotification(
                    request.title(),
                    request.content(),
                    type,
                    null
            );
            return;
        }

        // Gửi theo Role: lấy danh sách user theo roleName (DB lưu name không có prefix "ROLE_")
        String roleName = request.targetRole().toUpperCase();
        for (User u : userRepository.findAllByRoleName(roleName)) {
            notificationService.sendNotificationToUser(
                    u.getId(),
                    request.title(),
                    request.content(),
                    type,
                    null
            );
        }
    }

    @Override
    @Transactional
    public void sendToUser(AdminNotificationRequestDTO.SendToUser request) {
        notificationService.sendNotificationToUser(
                request.userId(),
                request.title(),
                request.content(),
                NotificationType.valueOf(request.type()),
                null
        );
    }
}

