package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminNotificationRequestDTO;

public interface IAdminNotificationService {
    void broadcast(AdminNotificationRequestDTO.Broadcast request);

    void sendToUser(AdminNotificationRequestDTO.SendToUser request);
}

