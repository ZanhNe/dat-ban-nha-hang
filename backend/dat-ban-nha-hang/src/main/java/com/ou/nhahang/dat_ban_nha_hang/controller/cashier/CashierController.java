package com.ou.nhahang.dat_ban_nha_hang.controller.cashier;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetServedSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetPayingSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierCompletePaymentRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.service.ICashierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cashier")
@RequiredArgsConstructor
public class CashierController {

        private final ICashierService cashierService;

        @GetMapping("/sessions")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<Page<CashierSessionListResponseDTO>>> getServedSessions(
                        @ModelAttribute @Valid CashierGetServedSessionsRequestDTO request, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<CashierSessionListResponseDTO> data = cashierService.getServedSessions(userId, request);
                return ResponseEntity.ok(ApiResponse.<Page<CashierSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách phiên bàn chờ thanh toán thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/sessions/paying")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<Page<CashierSessionListResponseDTO>>> getPayingSessions(
                        @ModelAttribute @Valid CashierGetPayingSessionsRequestDTO request, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<CashierSessionListResponseDTO> data = cashierService.getPayingSessions(userId, request);
                return ResponseEntity.ok(ApiResponse.<Page<CashierSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách phiên bàn đang thanh toán thành công")
                                .data(data)
                                .build());
        }

        @GetMapping("/sessions/{sessionId}")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<CashierSessionDetailResponseDTO>> getSessionDetailForPayment(
                        @PathVariable("sessionId") Long sessionId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                CashierSessionDetailResponseDTO data = cashierService.getSessionDetailForPayment(userId, sessionId);
                return ResponseEntity.ok(ApiResponse.<CashierSessionDetailResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build());
        }

        @PostMapping("/sessions/{sessionId}/payments")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<CashierInitiatePaymentResponseDTO>> initiatePayment(
                        @PathVariable("sessionId") Long sessionId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                CashierInitiatePaymentResponseDTO data = cashierService.initiatePayment(userId, sessionId);
                return ResponseEntity.ok(ApiResponse.<CashierInitiatePaymentResponseDTO>builder()
                                .status(201)
                                .message("Khởi tạo thanh toán thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/sessions/{sessionId}/payments/complete")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<CashierCompletePaymentResponseDTO>> completePayment(
                        @PathVariable("sessionId") Long sessionId,
                        @RequestBody @Valid CashierCompletePaymentRequestDTO request, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                CashierCompletePaymentResponseDTO data = cashierService.completePayment(userId, sessionId,
                                request);
                return ResponseEntity.ok(ApiResponse.<CashierCompletePaymentResponseDTO>builder()
                                .status(200)
                                .message("Hoàn tất thanh toán thành công")
                                .data(data)
                                .build());
        }
}
