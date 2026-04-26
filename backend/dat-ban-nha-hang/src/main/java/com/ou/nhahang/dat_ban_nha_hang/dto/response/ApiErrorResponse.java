package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.util.Map;

@Builder
public record ApiErrorResponse(
        int status,
        String code,
        String message,
        Map<String, String> errors) {
}
