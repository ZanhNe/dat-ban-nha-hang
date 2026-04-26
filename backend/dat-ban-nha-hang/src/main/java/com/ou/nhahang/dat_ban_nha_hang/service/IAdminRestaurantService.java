package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantListItemResponseDTO;
import org.springframework.data.domain.Page;

public interface IAdminRestaurantService {
    void approveRestaurant(Long restaurantId, AdminRestaurantRequestDTO.Approval request);

    Page<AdminRestaurantListItemResponseDTO> getRestaurants(int page, int limit, String status, String search);

    AdminRestaurantDetailResponseDTO getRestaurantDetail(Long restaurantId);

    void updateRestaurantStatus(Long restaurantId, AdminRestaurantRequestDTO.UpdateStatus request);

    void updateRestaurantCommission(Long restaurantId, AdminRestaurantRequestDTO.UpdateCommission request);

    void assignManager(Long restaurantId, Long userId);
}

