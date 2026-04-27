package com.ou.nhahang.dat_ban_nha_hang.controller.cashier;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetServedSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetPayingSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierCompletePaymentRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.service.ICashierService;
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
@RequestMapping("/api/v1/cashier")
@RequiredArgsConstructor
public class CashierController {

        private final ICashierService cashierService;

        @GetMapping("/sessions")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<List<CashierSessionListResponseDTO>>> getServedSessions(
                        @ModelAttribute @Valid CashierGetServedSessionsRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<CashierSessionListResponseDTO> dataPage = cashierService.getServedSessions(userId, request);

                Map<String, Object> meta = new HashMap<>();
                meta.put("page", dataPage.getNumber());
                meta.put("limit", dataPage.getSize());
                meta.put("totalItems", dataPage.getTotalElements());
                meta.put("totalPages", dataPage.getTotalPages());

                return ResponseEntity.ok(ApiResponse.<List<CashierSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Thành công")
                                .data(dataPage.getContent())
                                .meta(meta)
                                .build());
        }

        @GetMapping("/sessions/paying")
        @PreAuthorize("hasAnyAuthority('ROLE_CASHIER')")
        public ResponseEntity<ApiResponse<List<CashierSessionListResponseDTO>>> getPayingSessions(
                        @ModelAttribute @Valid CashierGetPayingSessionsRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<CashierSessionListResponseDTO> dataPage = cashierService.getPayingSessions(userId, request);

                Map<String, Object> meta = new HashMap<>();
                meta.put("page", dataPage.getNumber());
                meta.put("limit", dataPage.getSize());
                meta.put("totalItems", dataPage.getTotalElements());
                meta.put("totalPages", dataPage.getTotalPages());

                return ResponseEntity.ok(ApiResponse.<List<CashierSessionListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách phiên bàn đang thanh toán thành công")
                                .data(dataPage.getContent())
                                .meta(meta)
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
                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CashierInitiatePaymentResponseDTO>builder()
                                .status(201)
                                .message("Đã chuyển sang trạng thái chờ thanh toán")
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
                                .message("Thanh toán hoàn tất, bàn đã trống.")
                                .data(data)
                                .build());
        }
}