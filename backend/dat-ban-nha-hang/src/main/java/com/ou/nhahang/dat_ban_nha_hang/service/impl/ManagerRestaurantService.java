package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerRestaurantService;

import org.springframework.stereotype.Service;

@Service
public class ManagerRestaurantService implements IManagerRestaurantService {

    private final RestaurantRepository restaurantRepository;

    public ManagerRestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    private Restaurant getRestaurantIfManager(Long restaurantId, Long managerId) {
        return restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new BusinessException(
                        "Bạn không có quyền quản lý nhà hàng này hoặc nhà hàng không tồn tại"));
    }

    private ManagerRestaurantResponseDTO mapToDTO(Restaurant restaurant) {
        return ManagerRestaurantResponseDTO.builder()
                .restaurantId(restaurant.getId())
                .name(restaurant.getName())
                .logo(restaurant.getLogo())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .status(restaurant.getStatus().name())
                .baseDepositValue(restaurant.getBaseDepositValue())
                .depositPolicy(restaurant.getDepositPolicy().name())
                .dayOfWeek(restaurant.getDayOfWeek())
                .build();
    }

    @Override
    public ManagerRestaurantResponseDTO getRestaurantDetail(Long restaurantId, Long managerId) {
        Restaurant restaurant = getRestaurantIfManager(restaurantId, managerId);
        return mapToDTO(restaurant);
    }

    @Override
    public ManagerRestaurantResponseDTO updateRestaurant(Long restaurantId, Long managerId,
            ManagerRestaurantRequestDTO.UpdateRestaurant requestDTO) {
        Restaurant restaurant = getRestaurantIfManager(restaurantId, managerId);

        restaurant.setName(requestDTO.name());
        if (requestDTO.logo() != null) {
            restaurant.setLogo(requestDTO.logo());
        }
        restaurant.setDescription(requestDTO.description());
        restaurant.setAddress(requestDTO.address());
        restaurant.setBaseDepositValue(requestDTO.baseDepositValue());
        restaurant.setDepositPolicy(Restaurant.DepositType.valueOf(requestDTO.depositPolicy()));
        restaurant.setDayOfWeek(requestDTO.dayOfWeek());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return mapToDTO(updatedRestaurant);
    }

    @Override
    public void deleteRestaurant(Long restaurantId, Long managerId) {
        Restaurant restaurant = getRestaurantIfManager(restaurantId, managerId);
        restaurant.setStatus(Restaurant.RestaurantStatus.CLOSED);
        restaurantRepository.save(restaurant);
    }
}
