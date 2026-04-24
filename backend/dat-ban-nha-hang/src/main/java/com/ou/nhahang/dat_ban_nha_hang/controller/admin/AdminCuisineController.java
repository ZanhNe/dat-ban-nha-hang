package com.ou.nhahang.dat_ban_nha_hang.controller.admin;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminCuisineRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminCuisineResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminCuisineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/cuisines")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminCuisineController {

    private final IAdminCuisineService adminCuisineService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminCuisineResponseDTO>>> getCuisines() {
        List<AdminCuisineResponseDTO> data = adminCuisineService.getAll();
        ApiResponse<List<AdminCuisineResponseDTO>> response = ApiResponse.<List<AdminCuisineResponseDTO>>builder()
                .status(200)
                .message("Lấy danh sách danh mục thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminCuisineResponseDTO>> createCuisine(
            @Valid @RequestBody AdminCuisineRequestDTO.Upsert request) {
        AdminCuisineResponseDTO data = adminCuisineService.create(request);
        ApiResponse<AdminCuisineResponseDTO> response = ApiResponse.<AdminCuisineResponseDTO>builder()
                .status(201)
                .message("Tạo danh mục thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCuisineResponseDTO>> updateCuisine(
            @PathVariable("id") Long cuisineId,
            @Valid @RequestBody AdminCuisineRequestDTO.Upsert request) {
        AdminCuisineResponseDTO data = adminCuisineService.update(cuisineId, request);
        ApiResponse<AdminCuisineResponseDTO> response = ApiResponse.<AdminCuisineResponseDTO>builder()
                .status(200)
                .message("Cập nhật thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCuisine(@PathVariable("id") Long cuisineId) {
        adminCuisineService.delete(cuisineId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Xóa thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

