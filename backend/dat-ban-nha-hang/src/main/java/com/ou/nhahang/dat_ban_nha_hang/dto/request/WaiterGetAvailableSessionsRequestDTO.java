package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record WaiterGetAvailableSessionsRequestDTO(
        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0") Integer page,
        @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1") Integer limit,
        @Pattern(regexp = "^(ACTIVE|SERVING|SERVED|PAYING|COMPLETED)$", message = "Status session không hợp lệ")
        String status,
        Boolean unassigned
) {
    public WaiterGetAvailableSessionsRequestDTO {
        if (page == null || page < 0)
            page = 0;
        if (limit == null || limit < 1)
            limit = 10;
        if (status == null)
            status = "ACTIVE";
        if (unassigned == null)
            unassigned = true;
    }
}
