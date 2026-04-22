package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.Instant;
import java.util.Map;
import com.ou.nhahang.dat_ban_nha_hang.entity.NotificationType;
import lombok.Builder;

@Builder
public record NotificationResponseDTO(
        Long id,
        String title,
        String content,
        NotificationType type,
        boolean read,
        Map<String, Object> metadata,
        Instant createdAt
) {
}
