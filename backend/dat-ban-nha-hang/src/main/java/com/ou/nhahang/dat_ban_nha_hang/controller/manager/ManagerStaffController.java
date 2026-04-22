package com.ou.nhahang.dat_ban_nha_hang.controller.manager;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerStaffResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerStaffService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/manager")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ManagerStaffController {

    private final IManagerStaffService managerStaffService;

    @GetMapping("/restaurants/{restaurantId}/staffs")
    public ResponseEntity<ApiResponse<List<ManagerStaffResponseDTO>>> getStaffs(
            @PathVariable Long restaurantId,
            @Valid ManagerStaffRequestDTO.GetStaffs requestDTO,
            Authentication authentication) {

        Long managerId = (Long) authentication.getCredentials();
        Page<ManagerStaffResponseDTO> dataPage = managerStaffService.getStaffs(restaurantId, managerId, requestDTO);

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", dataPage.getNumber());
        meta.put("size", dataPage.getSize());
        meta.put("totalElements", dataPage.getTotalElements());
        meta.put("totalPages", dataPage.getTotalPages());

        ApiResponse<List<ManagerStaffResponseDTO>> response = ApiResponse.<List<ManagerStaffResponseDTO>>builder()
                .status(200)
                .message("Lấy danh sách nhân viên thành công")
                .data(dataPage.getContent())
                .meta(meta)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restaurants/{restaurantId}/staffs")
    public ResponseEntity<ApiResponse<ManagerStaffResponseDTO>> createStaff(
            @PathVariable Long restaurantId,
            @Valid @RequestBody ManagerStaffRequestDTO.CreateStaff requestDTO,
            Authentication authentication) {

        Long managerId = (Long) authentication.getCredentials();
        ManagerStaffResponseDTO data = managerStaffService.createStaff(restaurantId, managerId, requestDTO);

        ApiResponse<ManagerStaffResponseDTO> response = ApiResponse.<ManagerStaffResponseDTO>builder()
                .status(201)
                .message("Thêm nhân viên thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/staffs/{staffId}")
    public ResponseEntity<ApiResponse<ManagerStaffResponseDTO>> updateStaff(
            @PathVariable Long staffId,
            @Valid @RequestBody ManagerStaffRequestDTO.UpdateStaff requestDTO,
            Authentication authentication) {

        Long managerId = (Long) authentication.getCredentials();
        ManagerStaffResponseDTO data = managerStaffService.updateStaff(staffId, managerId, requestDTO);

        ApiResponse<ManagerStaffResponseDTO> response = ApiResponse.<ManagerStaffResponseDTO>builder()
                .status(200)
                .message("Cập nhật nhân viên thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/staffs/{staffId}")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(
            @PathVariable Long staffId,
            Authentication authentication) {

        Long managerId = (Long) authentication.getCredentials();
        managerStaffService.deleteStaff(staffId, managerId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Xóa nhân viên thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
