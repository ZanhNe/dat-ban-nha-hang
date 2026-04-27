package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistRejectBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistConfirmBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistRejectBookingResponseDTO;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistCancelBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistBookingListResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistBookingDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistCancelBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistCheckInResponseDTO;
import org.springframework.data.domain.Page;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistGetBookingsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistGetAwaitingBookingsRequestDTO;

public interface IReceptionistBookingService {
    ReceptionistConfirmBookingResponseDTO confirmBooking(Long bookingId);

    ReceptionistRejectBookingResponseDTO rejectBooking(Long bookingId, ReceptionistRejectBookingRequestDTO request);

    Page<ReceptionistBookingListResponseDTO> getBookings(Long userId, ReceptionistGetBookingsRequestDTO request);

    Page<ReceptionistBookingListResponseDTO> getAwaitingBookings(Long userId,
            ReceptionistGetAwaitingBookingsRequestDTO request);

    ReceptionistBookingDetailResponseDTO getBookingDetail(Long userId, Long bookingId);

    ReceptionistCheckInResponseDTO checkInBooking(Long userId, Long bookingId);

    ReceptionistCancelBookingResponseDTO cancelBooking(Long userId, Long bookingId,
            ReceptionistCancelBookingRequestDTO request);
}
