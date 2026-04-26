package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminUserRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminUserDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminUserResponseDTO;
import org.springframework.data.domain.Page;

public interface IAdminUserService {
    Page<AdminUserResponseDTO> getUsers(int page, int limit, String role, Long restaurantId, String status, String search);

    AdminUserDetailResponseDTO getUserDetail(Long userId);

    AdminUserResponseDTO createUser(AdminUserRequestDTO.Create request);

    AdminUserResponseDTO updateUser(Long userId, AdminUserRequestDTO.Update request);

    void updateUserWorkplace(Long userId, AdminUserRequestDTO.UpdateWorkplace request);
}

