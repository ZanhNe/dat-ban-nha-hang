package com.ou.nhahang.dat_ban_nha_hang.controller.receptionist;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistRejectBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistConfirmBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistRejectBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IReceptionistBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistCancelBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistGetBookingsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistGetAwaitingBookingsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistBookingListResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistBookingDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistCancelBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistCheckInResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/receptionist/bookings")
@RequiredArgsConstructor
public class ReceptionistBookingController {

        private final IReceptionistBookingService receptionistBookingService;

        @PatchMapping("/{bookingId}/confirm")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<ReceptionistConfirmBookingResponseDTO>> confirmBooking(
                        @PathVariable("bookingId") Long bookingId,
                        Authentication authentication) {

                Long userId = (Long) authentication.getCredentials();
                ReceptionistConfirmBookingResponseDTO data = receptionistBookingService.confirmBooking(userId,
                                bookingId);

                String message = data.depositAmount() == 0L
                                ? "Đã xác nhận yêu cầu đặt bàn (Không yêu cầu cọc)."
                                : "Đã xác nhận yêu cầu đặt bàn, chờ khách hàng thanh toán cọc trong 15 phút.";

                ApiResponse<ReceptionistConfirmBookingResponseDTO> response = ApiResponse
                                .<ReceptionistConfirmBookingResponseDTO>builder()
                                .status(200)
                                .message(message)
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{bookingId}/reject")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<ReceptionistRejectBookingResponseDTO>> rejectBooking(
                        @PathVariable("bookingId") Long bookingId,
                        @RequestBody @Valid ReceptionistRejectBookingRequestDTO request,
                        Authentication authentication) {

                Long userId = (Long) authentication.getCredentials();
                ReceptionistRejectBookingResponseDTO data = receptionistBookingService.rejectBooking(userId,
                                bookingId, request);

                ApiResponse<ReceptionistRejectBookingResponseDTO> response = ApiResponse
                                .<ReceptionistRejectBookingResponseDTO>builder()
                                .status(200)
                                .message("Đã từ chối yêu cầu đặt bàn.")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<List<ReceptionistBookingListResponseDTO>>> getBookings(
                        @ModelAttribute @Valid ReceptionistGetBookingsRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<ReceptionistBookingListResponseDTO> dataPage = receptionistBookingService
                                .getBookings(userId, request);

                Map<String, Object> meta = new HashMap<>();
                meta.put("page", dataPage.getNumber());
                meta.put("limit", dataPage.getSize());
                meta.put("totalItems", dataPage.getTotalElements());
                meta.put("totalPages", dataPage.getTotalPages());

                return ResponseEntity.ok(ApiResponse.<List<ReceptionistBookingListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách booking thành công")
                                .data(dataPage.getContent())
                                .meta(meta)
                                .build());
        }

        @GetMapping("/awaiting-confirmation")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<List<ReceptionistBookingListResponseDTO>>> getAwaitingConfirmationBookings(
                        @ModelAttribute @Valid ReceptionistGetAwaitingBookingsRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                Page<ReceptionistBookingListResponseDTO> dataPage = receptionistBookingService
                                .getAwaitingBookings(userId, request);

                Map<String, Object> meta = new HashMap<>();
                meta.put("page", dataPage.getNumber());
                meta.put("limit", dataPage.getSize());
                meta.put("totalItems", dataPage.getTotalElements());
                meta.put("totalPages", dataPage.getTotalPages());

                return ResponseEntity.ok(ApiResponse.<List<ReceptionistBookingListResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách yêu cầu đặt bàn chờ xác nhận thành công")
                                .data(dataPage.getContent())
                                .meta(meta)
                                .build());
        }

        @GetMapping("/{bookingId}")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<ReceptionistBookingDetailResponseDTO>> getBookingDetail(
                        @PathVariable("bookingId") Long bookingId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                ReceptionistBookingDetailResponseDTO data = receptionistBookingService
                                .getBookingDetail(userId, bookingId);
                return ResponseEntity.ok(ApiResponse.<ReceptionistBookingDetailResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/{bookingId}/check-in")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<ReceptionistCheckInResponseDTO>> checkInBooking(
                        @PathVariable("bookingId") Long bookingId, Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                ReceptionistCheckInResponseDTO data = receptionistBookingService.checkInBooking(userId,
                                bookingId);
                return ResponseEntity.ok(ApiResponse.<ReceptionistCheckInResponseDTO>builder()
                                .status(200)
                                .message("Check-in thành công")
                                .data(data)
                                .build());
        }

        @PatchMapping("/{bookingId}/cancel")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST')")
        public ResponseEntity<ApiResponse<ReceptionistCancelBookingResponseDTO>> cancelBooking(
                        @PathVariable("bookingId") Long bookingId,
                        @RequestBody @Valid ReceptionistCancelBookingRequestDTO request,
                        Authentication authentication) {
                Long userId = (Long) authentication.getCredentials();
                ReceptionistCancelBookingResponseDTO data = receptionistBookingService.cancelBooking(userId,
                                bookingId, request);
                return ResponseEntity.ok(ApiResponse.<ReceptionistCancelBookingResponseDTO>builder()
                                .status(200)
                                .message("Đã hủy booking thành công")
                                .data(data)
                                .build());
        }
}

