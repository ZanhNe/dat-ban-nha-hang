package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffManagementRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerStaffManagementResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerStaffResponseDTO;
import org.springframework.data.domain.Page;

public interface IManagerStaffService {
    Page<ManagerStaffResponseDTO> getStaffs(Long restaurantId, Long managerId,
            ManagerStaffRequestDTO.GetStaffs requestDTO);

    ManagerStaffResponseDTO createStaff(Long restaurantId, Long managerId,
            ManagerStaffRequestDTO.CreateStaff requestDTO);

    ManagerStaffResponseDTO updateStaff(Long staffId, Long managerId, ManagerStaffRequestDTO.UpdateStaff requestDTO);

    void deleteStaff(Long staffId, Long managerId);

    Page<ManagerStaffManagementResponseDTO> getStaffsByManager(Long managerId, ManagerStaffManagementRequestDTO.ListStaffs requestDTO);

    ManagerStaffManagementResponseDTO createStaffByManager(Long managerId, ManagerStaffManagementRequestDTO.CreateStaff requestDTO);

    void kickStaff(Long staffId, Long managerId);
}
