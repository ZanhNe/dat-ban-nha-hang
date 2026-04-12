package com.ou.nhahang.dat_ban_nha_hang.service;

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
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetBookingHistoryRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantReviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetBookingHistoryResponseDTO;

import org.springframework.data.domain.Page;

public interface ICustomerRestaurantService {
        TableSearchResponseDTO searchTablesExecute(Long restaurantId, TableSearchRequestDTO requestDTO);

        BookingResponseDTO bookingExecute(BookingRequestDTO requestDTO, Long userId, Long restaurantId);

        Page<SearchRestaurantResponseDTO> searchRestaurantsExecute(SearchRestaurantRequestDTO requestDTO);

        GetRestaurantDetailResponseDTO getRestaurantDetailExecute(GetRestaurantDetailRequestDTO requestDto);

        GetRestaurantMenuResponseDTO getRestaurantMenuExecute(Long restaurantId);

        CursorPaginationResult<GetRestaurantReviewResponseDTO> getRestaurantReviewsExecute(Long restaurantId,
                        GetRestaurantReviewRequestDTO request);

        GetRestaurantReviewResponseDTO createReviewExecute(Long restaurantId, Long userId,
                        CreateRestaurantReviewRequestDTO request);

        Page<GetBookingHistoryResponseDTO> getBookingHistoryExecute(Long userId,
                        GetBookingHistoryRequestDTO requestDTO);

}
