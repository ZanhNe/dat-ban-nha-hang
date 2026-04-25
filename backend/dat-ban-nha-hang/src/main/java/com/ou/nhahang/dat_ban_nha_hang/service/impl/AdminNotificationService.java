package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminNotificationRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.NotificationType;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminNotificationService;
import com.ou.nhahang.dat_ban_nha_hang.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNotificationService implements IAdminNotificationService {

    private final INotificationService notificationService;

    @Override
    @Transactional
    public void broadcast(AdminNotificationRequestDTO.Broadcast request) {
        notificationService.sendBroadcastNotification(
                request.title(),
                request.content(),
                NotificationType.valueOf(request.type()),
                null
        );
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

