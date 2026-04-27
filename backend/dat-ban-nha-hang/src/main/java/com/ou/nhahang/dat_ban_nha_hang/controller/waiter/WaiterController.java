package com.ou.nhahang.dat_ban_nha_hang.controller.waiter;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetAvailableSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetMySessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterCancelOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterConfirmOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterUpdateFoodItemStatusRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.service.IWaiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/waiter")
@RequiredArgsConstructor
public class WaiterController {

        private final IWaiterService waiterService;

        private Long getCurrentUserId() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) authentication.getPrincipal();
                return user.getId();
        }

        @GetMapping("/sessions")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<Page<WaiterSessionListResponseDTO>>> getAvailableSessions(
                        @ModelAttribute @Valid WaiterGetAvailableSessionsRequestDTO request) {
                Page<WaiterSessionListResponseDTO> data = waiterService.getAvailableSessions(getCurrentUserId(),
                                request);
                return ResponseEntity.ok(ApiResponse.<Page<WaiterSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách phiên bàn thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/sessions/me")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<Page<WaiterSessionListResponseDTO>>> getMyServingSessions(
                        @ModelAttribute @Valid WaiterGetMySessionsRequestDTO request) {
                Page<WaiterSessionListResponseDTO> data = waiterService.getMyServingSessions(getCurrentUserId(),
                                request);
                return ResponseEntity.ok(ApiResponse.<Page<WaiterSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách bàn đang phục vụ thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/sessions/{sessionId}")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterSessionDetailResponseDTO>> getSessionDetail(
                        @PathVariable("sessionId") Long sessionId) {
                WaiterSessionDetailResponseDTO data = waiterService.getSessionDetail(getCurrentUserId(), sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterSessionDetailResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/sessions/{sessionId}/assign")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterAssignSessionResponseDTO>> assignSession(
                        @PathVariable("sessionId") Long sessionId) {
                WaiterAssignSessionResponseDTO data = waiterService.assignSession(getCurrentUserId(), sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterAssignSessionResponseDTO>builder()
                                .status(200)
                                .message("Nhận bàn thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/menu")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterMenuResponseDTO>> getMenu() {
                WaiterMenuResponseDTO data = waiterService.getMenu(getCurrentUserId());
                return ResponseEntity.ok(ApiResponse.<WaiterMenuResponseDTO>builder()
                                .status(200)
                                .message("Lấy menu thành công")
                                .data(data)
                                .build());
        }

        @PostMapping("/sessions/{sessionId}/orders")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterCreateOrderResponseDTO>> createFoodOrder(
                        @PathVariable("sessionId") Long sessionId) {
                WaiterCreateOrderResponseDTO data = waiterService.createFoodOrder(getCurrentUserId(), sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterCreateOrderResponseDTO>builder()
                                .status(201)
                                .message("Tạo Order thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/orders/{orderId}")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterFoodOrderDetailResponseDTO>> getFoodOrderDetail(
                        @PathVariable("orderId") Long orderId) {
                WaiterFoodOrderDetailResponseDTO data = waiterService.getFoodOrderDetail(getCurrentUserId(), orderId);
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
                        @RequestBody @Valid WaiterConfirmOrderRequestDTO request) {
                WaiterConfirmOrderResponseDTO data = waiterService.confirmFoodOrder(getCurrentUserId(), orderId,
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
                        @RequestBody @Valid WaiterUpdateFoodItemStatusRequestDTO request) {
                WaiterUpdateFoodItemStatusResponseDTO data = waiterService.updateFoodItemStatus(getCurrentUserId(),
                                itemId, request);
                return ResponseEntity.ok(ApiResponse.<WaiterUpdateFoodItemStatusResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật món ăn thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/orders/{orderId}/complete")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterFoodOrderDetailResponseDTO>> completeFoodOrder(
                        @PathVariable("orderId") Long orderId) {
                WaiterFoodOrderDetailResponseDTO data = waiterService.completeFoodOrder(getCurrentUserId(), orderId);
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
                        @RequestBody @Valid WaiterCancelOrderRequestDTO request) {
                WaiterCancelOrderResponseDTO data = waiterService.cancelFoodOrder(getCurrentUserId(), orderId, request);
                return ResponseEntity.ok(ApiResponse.<WaiterCancelOrderResponseDTO>builder()
                                .status(200)
                                .message("Đã hủy order thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/sessions/{sessionId}/serve-complete")
        @PreAuthorize("hasAuthority('ROLE_WAITER')")
        public ResponseEntity<ApiResponse<WaiterServeCompleteResponseDTO>> completeServiceSession(
                        @PathVariable("sessionId") Long sessionId) {
                WaiterServeCompleteResponseDTO data = waiterService.completeServiceSession(getCurrentUserId(),
                                sessionId);
                return ResponseEntity.ok(ApiResponse.<WaiterServeCompleteResponseDTO>builder()
                                .status(200)
                                .message("Hoàn tất phục vụ, chờ thanh toán")
                                .data(data)
                                .build());
        }
}
