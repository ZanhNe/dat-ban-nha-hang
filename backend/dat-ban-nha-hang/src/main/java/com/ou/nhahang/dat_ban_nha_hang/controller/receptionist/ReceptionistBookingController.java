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

@RestController
@RequestMapping("/api/v1/receptionist/bookings")
@RequiredArgsConstructor
public class ReceptionistBookingController {

        private final IReceptionistBookingService receptionistBookingService;

        @PatchMapping("/{bookingId}/confirm")
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_MANAGER')")
        public ResponseEntity<ApiResponse<ReceptionistConfirmBookingResponseDTO>> confirmBooking(
                        @PathVariable("bookingId") Long bookingId) {

                ReceptionistConfirmBookingResponseDTO data = receptionistBookingService.confirmBooking(bookingId);

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
        @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_MANAGER')")
        public ResponseEntity<ApiResponse<ReceptionistRejectBookingResponseDTO>> rejectBooking(
                        @PathVariable("bookingId") Long bookingId,
                        @RequestBody @Valid ReceptionistRejectBookingRequestDTO request) {

                ReceptionistRejectBookingResponseDTO data = receptionistBookingService.rejectBooking(bookingId,
                                request);

                ApiResponse<ReceptionistRejectBookingResponseDTO> response = ApiResponse
                                .<ReceptionistRejectBookingResponseDTO>builder()
                                .status(200)
                                .message("Đã từ chối yêu cầu đặt bàn.")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }
}
