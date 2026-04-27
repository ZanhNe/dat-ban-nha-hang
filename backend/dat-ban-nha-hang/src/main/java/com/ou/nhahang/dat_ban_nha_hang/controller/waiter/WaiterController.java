package com.ou.nhahang.dat_ban_nha_hang.controller.waiter;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetAvailableSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetMySessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterCancelOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterConfirmOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterUpdateFoodItemStatusRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.service.IWaiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/waiter")
@RequiredArgsConstructor
public class WaiterController {

        private final IWaiterService waiterService;

        @GetMapping("/sessions")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<List<WaiterSessionListResponseDTO>>> getAvailableSessions(
                        @ModelAttribute @Valid WaiterGetAvailableSessionsRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<WaiterSessionListResponseDTO> dataPage = waiterService.getAvailableSessions(userId, request);

                Map<String, Object> meta = new HashMap<>();
                meta.put("page", dataPage.getNumber());
                meta.put("limit", dataPage.getSize());
                meta.put("totalItems", dataPage.getTotalElements());
                meta.put("totalPages", dataPage.getTotalPages());

                return ResponseEntity.ok(ApiResponse.<List<WaiterSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách phiên bàn thành công")
                                .data(dataPage.getContent())
                                .meta(meta)
                                .build());
        }

        @GetMapping("/sessions/me")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<List<WaiterSessionListResponseDTO>>> getMyServingSessions(
                        @ModelAttribute @Valid WaiterGetMySessionsRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<WaiterSessionListResponseDTO> dataPage = waiterService.getMyServingSessions(userId, request);

                Map<String, Object> meta = new HashMap<>();
                meta.put("page", dataPage.getNumber());
                meta.put("limit", dataPage.getSize());
                meta.put("totalItems", dataPage.getTotalElements());
                meta.put("totalPages", dataPage.getTotalPages());

                return ResponseEntity.ok(ApiResponse.<List<WaiterSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách bàn đang phục vụ thành công")
                                .data(dataPage.getContent())
                                .meta(meta)
                                .build());
        }

        @GetMapping("/sessions/{sessionId}")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterSessionDetailResponseDTO>> getSessionDetail(
                        @PathVariable("sessionId") Long sessionId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterSessionDetailResponseDTO data = waiterService.getSessionDetail(userId, sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterSessionDetailResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/sessions/{sessionId}/assign")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterAssignSessionResponseDTO>> assignSession(
                        @PathVariable("sessionId") Long sessionId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterAssignSessionResponseDTO data = waiterService.assignSession(userId, sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterAssignSessionResponseDTO>builder()
                                .status(200)
                                .message("Nhận bàn thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/menu")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterMenuResponseDTO>> getMenu(Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterMenuResponseDTO data = waiterService.getMenu(userId);
                return ResponseEntity.ok(ApiResponse.<WaiterMenuResponseDTO>builder()
                                .status(200)
                                .message("Lấy menu thành công")
                                .data(data)
                                .build());
        }

        @PostMapping("/sessions/{sessionId}/orders")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterCreateOrderResponseDTO>> createFoodOrder(
                        @PathVariable("sessionId") Long sessionId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterCreateOrderResponseDTO data = waiterService.createFoodOrder(userId, sessionId);
                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<WaiterCreateOrderResponseDTO>builder()
                                .status(201)
                                .message("Tạo Order thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/orders/{orderId}")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterFoodOrderDetailResponseDTO>> getFoodOrderDetail(
                        @PathVariable("orderId") Long orderId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterFoodOrderDetailResponseDTO data = waiterService.getFoodOrderDetail(userId, orderId);
                return ResponseEntity.ok(ApiResponse.<WaiterFoodOrderDetailResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build());
        }

        @PostMapping("/orders/{orderId}/confirm-items")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterConfirmOrderResponseDTO>> confirmFoodOrder(
                        @PathVariable("orderId") Long orderId,
                        @RequestBody @Valid WaiterConfirmOrderRequestDTO request, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterConfirmOrderResponseDTO data = waiterService.confirmFoodOrder(userId, orderId,
                                request);
                return ResponseEntity.ok(ApiResponse.<WaiterConfirmOrderResponseDTO>builder()
                                .status(200)
                                .message("Đã gửi bếp thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/food-items/{itemId}/status")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterUpdateFoodItemStatusResponseDTO>> updateFoodItemStatus(
                        @PathVariable("itemId") Long itemId,
                        @RequestBody @Valid WaiterUpdateFoodItemStatusRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterUpdateFoodItemStatusResponseDTO data = waiterService.updateFoodItemStatus(userId, itemId,
                                request);
                return ResponseEntity.ok(ApiResponse.<WaiterUpdateFoodItemStatusResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật món ăn thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/orders/{orderId}/complete")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterFoodOrderDetailResponseDTO>> completeFoodOrder(
                        @PathVariable("orderId") Long orderId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterFoodOrderDetailResponseDTO data = waiterService.completeFoodOrder(userId, orderId);
                return ResponseEntity.ok(ApiResponse.<WaiterFoodOrderDetailResponseDTO>builder()
                                .status(200)
                                .message("Đã hoàn tất FoodOrder")
                                .data(data)
                                .build());
        }

        @PatchMapping("/orders/{orderId}/cancel")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterCancelOrderResponseDTO>> cancelFoodOrder(
                        @PathVariable("orderId") Long orderId,
                        @RequestBody @Valid WaiterCancelOrderRequestDTO request, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterCancelOrderResponseDTO data = waiterService.cancelFoodOrder(userId, orderId, request);
                return ResponseEntity.ok(ApiResponse.<WaiterCancelOrderResponseDTO>builder()
                                .status(200)
                                .message("Đã hủy order thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/sessions/{sessionId}/serve-complete")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterServeCompleteResponseDTO>> completeServiceSession(
                        @PathVariable("sessionId") Long sessionId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                WaiterServeCompleteResponseDTO data = waiterService.completeServiceSession(userId, sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterServeCompleteResponseDTO>builder()
                                .status(200)
                                .message("Hoàn tất phục vụ, chờ thanh toán")
                                .data(data)
                                .build());
        }
}
