package com.ou.nhahang.dat_ban_nha_hang.controller;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.PaymentInitResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/users/me/bookings/pending-payment")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'CUSTOMER')")
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

    @PostMapping("/bookings/{bookingId}/payments/initiate")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<PaymentInitResponseDTO>> initiatePayment(@PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        ApiResponse<PaymentInitResponseDTO> response = ApiResponse.<PaymentInitResponseDTO>builder()
                .status(201)
                .message("Khởi tạo thanh toán thành công")
                .data(paymentService.initiatePayment(bookingId, userId))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping("/bookings/{bookingId}/payments/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'RECEPTIONIST', 'ROLE_MANAGER', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> approvePayment(@PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        paymentService.approvePayment(bookingId, userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(200)
                .message("Thanh toán thành công")
                .data("Payment approved successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bookings/{bookingId}/payments/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'RECEPTIONIST', 'ROLE_MANAGER', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> rejectPayment(@PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        paymentService.rejectPayment(bookingId, userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(200)
                .message("Đã từ chối thanh toán")
                .data("Payment rejected successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
