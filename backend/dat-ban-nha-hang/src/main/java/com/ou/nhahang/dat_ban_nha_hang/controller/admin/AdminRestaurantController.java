package com.ou.nhahang.dat_ban_nha_hang.controller.admin;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminUserRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantListItemResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminRestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/restaurants")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminRestaurantController {

    private final IAdminRestaurantService adminRestaurantService;

    @PatchMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<Map<String, Object>>> approveRestaurant(
            @PathVariable("id") Long restaurantId,
            @Valid @RequestBody AdminRestaurantRequestDTO.Approval request) {

        adminRestaurantService.approveRestaurant(restaurantId, request);

        String responseStatus = "APPROVED".equalsIgnoreCase(request.status()) ? "APPROVED" : "REJECTED";
        String message = "APPROVED".equalsIgnoreCase(request.status())
                ? "Đã phê duyệt nhà hàng thành công."
                : "Đã từ chối nhà hàng.";

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message(message)
                .data(Map.of(
                        "restaurantId", restaurantId,
                        "status", responseStatus))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRestaurantListItemResponseDTO>>> getRestaurants(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search) {

        Page<AdminRestaurantListItemResponseDTO> dataPage = adminRestaurantService.getRestaurants(page, limit, status,
                search);

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", dataPage.getNumber());
        meta.put("limit", dataPage.getSize());
        meta.put("totalItems", dataPage.getTotalElements());
        meta.put("totalPages", dataPage.getTotalPages());

        ApiResponse<List<AdminRestaurantListItemResponseDTO>> response = ApiResponse
                .<List<AdminRestaurantListItemResponseDTO>>builder()
                .status(200)
                .message("Lấy danh sách nhà hàng thành công")
                .data(dataPage.getContent())
                .meta(meta)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminRestaurantDetailResponseDTO>> getRestaurantDetail(
            @PathVariable("id") Long restaurantId) {

        AdminRestaurantDetailResponseDTO data = adminRestaurantService.getRestaurantDetail(restaurantId);

        ApiResponse<AdminRestaurantDetailResponseDTO> response = ApiResponse.<AdminRestaurantDetailResponseDTO>builder()
                .status(200)
                .message("Thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRestaurantStatus(
            @PathVariable("id") Long restaurantId,
            @Valid @RequestBody AdminRestaurantRequestDTO.UpdateStatus request) {

        adminRestaurantService.updateRestaurantStatus(restaurantId, request);

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("Cập nhật trạng thái thành công")
                .data(Map.of(
                        "restaurantId", restaurantId,
                        "status", request.status()))
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/commission")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRestaurantCommission(
            @PathVariable("id") Long restaurantId,
            @Valid @RequestBody AdminRestaurantRequestDTO.UpdateCommission request) {

        adminRestaurantService.updateRestaurantCommission(restaurantId, request);

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("Cấu hình hoa hồng thành công")
                .data(Map.of(
                        "restaurantId", restaurantId,
                        "commissionType", request.commissionType(),
                        "baseCommissionValue", request.baseCommissionValue()))
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{restaurantId}/manager")
    public ResponseEntity<ApiResponse<Void>> assignManager(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AdminUserRequestDTO.AssignManager request) {
        adminRestaurantService.assignManager(restaurantId, request.userId());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã bổ nhiệm Manager cho nhà hàng thành công.")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

