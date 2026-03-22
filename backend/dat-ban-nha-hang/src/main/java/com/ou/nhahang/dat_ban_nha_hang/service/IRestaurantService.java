package com.ou.nhahang.dat_ban_nha_hang.service;

import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.SearchRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantMenuResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.SearchRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantReviewRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantReviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;

public interface IRestaurantService {
    TableSearchResponseDTO searchTablesExecute(Long restaurantId, TableSearchRequestDTO requestDTO);

    BookingResponseDTO bookingExecute(BookingRequestDTO requestDTO, Long userId, Long restaurantId);

    org.springframework.data.domain.Page<SearchRestaurantResponseDTO> searchRestaurantsExecute(SearchRestaurantRequestDTO requestDTO);

    GetRestaurantDetailResponseDTO getRestaurantDetailExecute(GetRestaurantDetailRequestDTO requestDto);

    GetRestaurantMenuResponseDTO getRestaurantMenuExecute(Long restaurantId);

    CursorPaginationResult<GetRestaurantReviewResponseDTO> getRestaurantReviewsExecute(Long restaurantId, GetRestaurantReviewRequestDTO request);

    GetRestaurantReviewResponseDTO createReviewExecute(Long restaurantId, Long userId, com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO request);
}
