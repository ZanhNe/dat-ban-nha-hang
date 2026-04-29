package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminRestaurantRequestDTO() {

        public record Approval(
                        @NotBlank(message = "Trạng thái phê duyệt không được để trống") @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "Trạng thái phê duyệt phải là APPROVED hoặc REJECTED") String status) {
        }

        public record UpdateStatus(
                        @NotBlank(message = "Trạng thái nhà hàng không được để trống") @Pattern(regexp = "^(OPENING|CLOSED|PENDING|SUSPENDED|REJECTED)$", message = "Trạng thái không hợp lệ") String status) {
        }

        public record UpdateCommission(
                        @NotBlank(message = "Loại hoa hồng không được để trống") @Pattern(regexp = "^(PERCENTAGE|FIXED)$", message = "Loại hoa hồng phải là PERCENTAGE hoặc FIXED") String commissionType,

                        @NotNull(message = "Giá trị hoa hồng không được thiếu") @Min(value = 0, message = "Giá trị hoa hồng không được âm") Long baseCommissionValue) {
        }
}
