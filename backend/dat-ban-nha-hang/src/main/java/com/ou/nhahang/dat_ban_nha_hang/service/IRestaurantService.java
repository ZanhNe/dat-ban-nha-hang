package com.ou.nhahang.dat_ban_nha_hang.service;

import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.SearchRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.SearchRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;

public interface IRestaurantService {
    TableSearchResponseDTO searchTablesExecute(Long restaurantId, TableSearchRequestDTO requestDTO);

    BookingResponseDTO bookingExecute(BookingRequestDTO requestDTO, Long userId, Long restaurantId);

    List<SearchRestaurantResponseDTO> searchRestaurantsExecute(SearchRestaurantRequestDTO requestDTO);

    GetRestaurantDetailResponseDTO getRestaurantDetailExecute(com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO requestDto);
}
