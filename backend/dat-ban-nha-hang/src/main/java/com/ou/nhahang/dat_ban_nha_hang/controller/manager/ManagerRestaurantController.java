package com.ou.nhahang.dat_ban_nha_hang.controller.manager;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerRestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/manager/restaurant")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ManagerRestaurantController {

        private final IManagerRestaurantService managerRestaurantService;

        @GetMapping
        public ResponseEntity<ApiResponse<ManagerRestaurantResponseDTO>> getRestaurantInfo(
                        Authentication authentication) {

                Long managerId = (Long) authentication.getCredentials();
                ManagerRestaurantResponseDTO data = managerRestaurantService.getRestaurantDetail(managerId);

                ApiResponse<ManagerRestaurantResponseDTO> response = ApiResponse.<ManagerRestaurantResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PutMapping
        public ResponseEntity<ApiResponse<ManagerRestaurantResponseDTO>> updateRestaurantInfo(
                        @Valid @RequestBody ManagerRestaurantRequestDTO.UpdateRestaurant requestDTO,
                        Authentication authentication) {

                Long managerId = (Long) authentication.getCredentials();
                ManagerRestaurantResponseDTO data = managerRestaurantService.updateRestaurant(managerId,
                                requestDTO);

                ApiResponse<ManagerRestaurantResponseDTO> response = ApiResponse.<ManagerRestaurantResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

}
