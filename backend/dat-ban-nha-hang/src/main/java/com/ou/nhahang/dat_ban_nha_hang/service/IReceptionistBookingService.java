package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ReceptionistRejectBookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistConfirmBookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ReceptionistRejectBookingResponseDTO;

public interface IReceptionistBookingService {
    ReceptionistConfirmBookingResponseDTO confirmBooking(Long bookingId);

    ReceptionistRejectBookingResponseDTO rejectBooking(Long bookingId, ReceptionistRejectBookingRequestDTO request);
}
