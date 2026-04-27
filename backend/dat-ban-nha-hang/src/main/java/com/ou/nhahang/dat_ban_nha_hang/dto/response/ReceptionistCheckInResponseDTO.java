package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record ReceptionistCheckInResponseDTO(
        Long sessionId,
        List<Long> tableIds,
        String status
) {}
