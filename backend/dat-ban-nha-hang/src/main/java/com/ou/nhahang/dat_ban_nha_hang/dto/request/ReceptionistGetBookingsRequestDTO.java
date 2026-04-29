package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ReceptionistGetBookingsRequestDTO(
        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0") Integer page,
        @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1") Integer limit,
        @Pattern(
                regexp = "^(AWAITING_CONFIRMATION|PENDING_PAYMENT|CONFIRMED|CUSTOMER_ARRIVED|SERVING|SERVED|COMPLETED|REJECTED|EXPIRED|CANCELLED|FAILED)$",
                message = "Trạng thái booking không hợp lệ"
        )
        String status,
        @Pattern(
                regexp = "^bookingTime\\.startTime,(asc|desc)$",
                message = "Sort chỉ hỗ trợ bookingTime.startTime,asc hoặc bookingTime.startTime,desc"
        )
        String sort
) {
    public ReceptionistGetBookingsRequestDTO {
        if (page == null || page < 0)
            page = 0;
        if (limit == null || limit < 1)
            limit = 10;
        if (status == null)
            status = "CONFIRMED";
        if (sort == null || sort.isEmpty())
            sort = "bookingTime.startTime,asc";
    }
}
