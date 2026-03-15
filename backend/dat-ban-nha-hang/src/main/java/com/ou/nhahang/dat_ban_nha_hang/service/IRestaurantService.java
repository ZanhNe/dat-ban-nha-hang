package com.ou.nhahang.dat_ban_nha_hang.service;

import java.time.LocalTime;
import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.dto.RestaurantTableDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;

public interface IRestaurantService {
    List<RestaurantTableDTO> findTableExecute(LocalTime time, Long quantity, Long restaurantId);

    BookingResponseDTO bookingExecute(BookingRequestDTO requestDTO, Long userId);
}

