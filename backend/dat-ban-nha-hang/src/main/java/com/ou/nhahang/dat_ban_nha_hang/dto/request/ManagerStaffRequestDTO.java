package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ManagerStaffRequestDTO() {

        public record CreateStaff(
                        @NotBlank(message = "Username không được để trống") String username,

                        @NotBlank(message = "Mật khẩu không được để trống") String password,

                        @NotBlank(message = "Họ và tên không được để trống") String fullName,

                        String email,

                        @NotBlank(message = "Số điện thoại không được để trống") String phone,

                        @NotNull(message = "Cần chỉ định role cho nhân viên") Long roleId) {
        }

        public record GetStaffs(
                        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0") Integer page,
                        @Min(value = 10, message = "Số lượng trên mỗi trang phải lớn hơn 0") @Max(value = 50, message = "Số lượng trên mỗi trang phải nhỏ hơn hoặc bằng 50") Integer limit) {

                public GetStaffs {
                        if (page == null || page < 0)
                                page = 0;
                        if (limit == null || limit <= 0)
                                limit = 10;
                        if (limit > 50)
                                limit = 50;
                }
        }

        public record UpdateStaff(
                        @NotBlank(message = "Họ và tên không được để trống") String fullName,

                        String email,

                        @NotBlank(message = "Số điện thoại không được để trống") String phone,

                        @NotBlank(message = "Trạng thái không được để trống") @Pattern(regexp = "^(ACTIVE|BANNED)$", message = "Trạng thái phải là ACTIVE hoặc BANNED") String status,

                        @NotNull(message = "Cần chỉ định role cho nhân viên") Long roleId) {
        }
}
