package com.ou.nhahang.dat_ban_nha_hang.controller.manager;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerTableAreaRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerTableRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTableAreaResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTableResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerTableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/manager")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ManagerTableController {

        private final IManagerTableService managerTableService;

        @GetMapping("/table-areas")
        public ResponseEntity<ApiResponse<List<ManagerTableAreaResponseDTO>>> getTableAreasByManager(
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerTableAreaResponseDTO> data = managerTableService.getTableAreasByManager(managerId);

                ApiResponse<List<ManagerTableAreaResponseDTO>> response = ApiResponse
                                .<List<ManagerTableAreaResponseDTO>>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/table-areas")
        public ResponseEntity<ApiResponse<ManagerTableAreaResponseDTO>> createTableAreaByManager(
                        @Valid @RequestBody ManagerTableAreaRequestDTO.CreateOrUpdateTableArea requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerTableAreaResponseDTO data = managerTableService.createTableAreaByManager(managerId, requestDTO);

                ApiResponse<ManagerTableAreaResponseDTO> response = ApiResponse.<ManagerTableAreaResponseDTO>builder()
                                .status(201)
                                .message("Thêm khu vực bàn ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/table-areas/{tableAreaId}")
        public ResponseEntity<ApiResponse<ManagerTableAreaResponseDTO>> updateTableArea(
                        @PathVariable Long tableAreaId,
                        @Valid @RequestBody ManagerTableAreaRequestDTO.CreateOrUpdateTableArea requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerTableAreaResponseDTO data = managerTableService.updateTableArea(tableAreaId, managerId,
                                requestDTO);

                ApiResponse<ManagerTableAreaResponseDTO> response = ApiResponse.<ManagerTableAreaResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/table-areas/{tableAreaId}")
        public ResponseEntity<ApiResponse<Void>> deleteTableArea(
                        @PathVariable Long tableAreaId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerTableService.deleteTableArea(tableAreaId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa khu vực thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/table-areas/{tableAreaId}/tables")
        public ResponseEntity<ApiResponse<List<ManagerTableResponseDTO>>> getTables(
                        @PathVariable Long tableAreaId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerTableResponseDTO> data = managerTableService.getTables(tableAreaId, managerId);

                ApiResponse<List<ManagerTableResponseDTO>> response = ApiResponse
                                .<List<ManagerTableResponseDTO>>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/table-areas/{tableAreaId}/tables")
        public ResponseEntity<ApiResponse<ManagerTableResponseDTO>> createTable(
                        @PathVariable Long tableAreaId,
                        @Valid @RequestBody ManagerTableRequestDTO.CreateOrUpdateTable requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerTableResponseDTO data = managerTableService.createTable(tableAreaId, managerId, requestDTO);

                ApiResponse<ManagerTableResponseDTO> response = ApiResponse.<ManagerTableResponseDTO>builder()
                                .status(201)
                                .message("Thêm bàn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/tables/{tableId}")
        public ResponseEntity<ApiResponse<ManagerTableResponseDTO>> updateTable(
                        @PathVariable Long tableId,
                        @Valid @RequestBody ManagerTableRequestDTO.CreateOrUpdateTable requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerTableResponseDTO data = managerTableService.updateTable(tableId, managerId, requestDTO);

                ApiResponse<ManagerTableResponseDTO> response = ApiResponse.<ManagerTableResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/tables/{tableId}")
        public ResponseEntity<ApiResponse<Void>> deleteTable(
                        @PathVariable Long tableId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerTableService.deleteTable(tableId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa bàn thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }
}
