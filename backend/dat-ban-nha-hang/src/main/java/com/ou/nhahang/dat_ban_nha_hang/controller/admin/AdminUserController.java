package com.ou.nhahang.dat_ban_nha_hang.controller.admin;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminUserRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminUserDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminUserResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final IAdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponseDTO>>> getUsers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "restaurantId", required = false) Long restaurantId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search) {

        Page<AdminUserResponseDTO> dataPage = adminUserService.getUsers(page, limit, role, restaurantId, status, search);

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", dataPage.getNumber());
        meta.put("limit", dataPage.getSize());
        meta.put("totalItems", dataPage.getTotalElements());
        meta.put("totalPages", dataPage.getTotalPages());

        ApiResponse<List<AdminUserResponseDTO>> response = ApiResponse.<List<AdminUserResponseDTO>>builder()
                .status(200)
                .message("Thành công")
                .data(dataPage.getContent())
                .meta(meta)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponseDTO>> getUserDetail(@PathVariable Long userId) {
        AdminUserDetailResponseDTO data = adminUserService.getUserDetail(userId);
        ApiResponse<AdminUserDetailResponseDTO> response = ApiResponse.<AdminUserDetailResponseDTO>builder()
                .status(200)
                .message("Thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> createUser(@Valid @RequestBody AdminUserRequestDTO.Create request) {
        AdminUserResponseDTO data = adminUserService.createUser(request);
        ApiResponse<AdminUserResponseDTO> response = ApiResponse.<AdminUserResponseDTO>builder()
                .status(201)
                .message("Thêm nhân viên thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRequestDTO.Update request) {
        AdminUserResponseDTO data = adminUserService.updateUser(userId, request);
        ApiResponse<AdminUserResponseDTO> response = ApiResponse.<AdminUserResponseDTO>builder()
                .status(200)
                .message("Cập nhật nhân viên thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/workplace")
    public ResponseEntity<ApiResponse<Void>> updateWorkplace(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRequestDTO.UpdateWorkplace request) {
        adminUserService.updateUserWorkplace(userId, request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã điều chuyển nhân sự thành công.")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

