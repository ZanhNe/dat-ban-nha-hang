package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRestaurantResponseDTO;

public interface IManagerRestaurantService {
    ManagerRestaurantResponseDTO getRestaurantDetail(Long managerId);

    ManagerRestaurantResponseDTO updateRestaurant(Long managerId,
            ManagerRestaurantRequestDTO.UpdateRestaurant requestDTO);
}
