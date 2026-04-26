package com.ou.nhahang.dat_ban_nha_hang.controller;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.PaymentInitResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IPaymentService;
import com.ou.nhahang.dat_ban_nha_hang.utils.VNPayUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

        private final IPaymentService paymentService;

        @GetMapping("/users/me/bookings/pending-payment")
        @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
        public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getPendingBookings(
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                ApiResponse<List<BookingResponseDTO>> response = ApiResponse.<List<BookingResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách đặt bàn thành công")
                                .data(paymentService.getPendingBookingsForUser(userId))
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/bookings/{bookingId}/transactions/initiate-online")
        @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
        public ResponseEntity<ApiResponse<PaymentInitResponseDTO>> initiateTransaction(HttpServletRequest request,
                        @PathVariable Long bookingId,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                String ipAddress = VNPayUtil.getIpAddress(request);
                PaymentInitResponseDTO paymentInitResponseDTO = paymentService.initiateTransaction(bookingId, userId,
                                ipAddress);
                ApiResponse<PaymentInitResponseDTO> response = ApiResponse.<PaymentInitResponseDTO>builder()
                                .status(201)
                                .message("Khởi tạo giao dịch thành công")
                                .data(paymentInitResponseDTO)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // @PostMapping("/bookings/{bookingId}/transactions/initiate-cash")
        // @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
        // public ResponseEntity<ApiResponse<PaymentInitResponseDTO>>
        // initiateCashTransaction(@PathVariable Long bookingId,
        // Authentication authentication) {
        // Long userId = (Long) authentication.getCredentials();
        // ApiResponse<PaymentInitResponseDTO> response =
        // ApiResponse.<PaymentInitResponseDTO>builder()
        // .status(201)
        // .message("Thanh toán tiền mặt thành công")
        // .data(null)
        // .build();
        // return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // }

        @PostMapping("/transactions/webhook")
        public ResponseEntity<Map<String, String>> handleWebhook(@RequestParam Map<String, String> params) {
                Map<String, String> result = paymentService.handleWebhook(params);
                return ResponseEntity.status(HttpStatus.OK).body(result);
        }

}
