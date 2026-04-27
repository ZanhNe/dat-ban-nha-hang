package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WaiterSessionListResponseDTO(
        Long sessionId,
        List<String> tableLabels,
        String customerName,
        Integer numberOfPeople,
        String status,
        LocalDateTime createdAt
) {}
