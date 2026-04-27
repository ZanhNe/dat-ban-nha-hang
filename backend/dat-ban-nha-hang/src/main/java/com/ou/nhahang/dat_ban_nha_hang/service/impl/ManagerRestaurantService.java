package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerRestaurantService;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerRestaurantService implements IManagerRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

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
    @Transactional(readOnly = true)
    public ManagerRestaurantResponseDTO getRestaurantDetail(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager không tồn tại"));
        Restaurant restaurant = manager.getWorkplace();
        if (restaurant == null) {
            throw new BusinessException("Manager này chưa được phân công nhà hàng nào");
        }
        return mapToDTO(restaurant);
    }

    @Override
    @Transactional
    public ManagerRestaurantResponseDTO updateRestaurant(Long managerId,
            ManagerRestaurantRequestDTO.UpdateRestaurant requestDTO) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager không tồn tại"));
        Restaurant restaurant = manager.getWorkplace();
        if (restaurant == null) {
            throw new BusinessException("Manager này chưa được phân công nhà hàng nào");
        }

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
    @Transactional
    public void deleteRestaurant(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager không tồn tại"));
        Restaurant restaurant = manager.getWorkplace();
        if (restaurant == null) {
            throw new BusinessException("Manager này chưa được phân công nhà hàng nào");
        }

        restaurant.setStatus(Restaurant.RestaurantStatus.CLOSED);
        restaurantRepository.save(restaurant);
    }
}
